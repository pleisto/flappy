import {
  type FindFlappyFunction,
  type AnyFlappyFunction,
  type FlappyAgentConfig,
  type FlappyFunctionNames
} from './flappy-agent.interface'
import { SynthesizedFunction } from './synthesized-function'
import { InvokeFunction } from './invoke-function'
import { type LLMBase } from './llm/llm-base'
import { type ChatMLResponse, type ChatMLMessage } from './llm/interface'
import { STEP_PREFIX } from './flappy-agent.constants'
import { z } from './flappy-type'
import { convertJsonToYaml, zodToCleanJsonSchema } from './utils'
import { evalPythonCode } from '@pleisto/flappy-nodejs-bindings'

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
const lanOutputSchema = (enableCoT: boolean) => {
  const baseStep = {
    id: z.number().int().positive().describe('Increment id starting from 1'),
    functionName: z.string(),
    args: z
      .record(z.any())
      .describe(
        `an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '${STEP_PREFIX}' + the ID of the previous step (e.g. '${STEP_PREFIX}1'). If an argument's value is derived from the **previous** step's function's return value's properties, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema.`
      )
  }
  const thought = enableCoT
    ? {
        thought: z.string().describe('The thought why this step is needed.')
      }
    : ({} as any)
  return z
    .array(
      z.object({
        ...thought,
        ...baseStep
      })
    )
    .describe('An array storing the steps.')
}

const DEFAULT_RETRY = 1

export class FlappyAgent<
  TFunctions extends AnyFlappyFunction[] = AnyFlappyFunction[],
  TNames extends string = FlappyFunctionNames<TFunctions>
> {
  config: any
  llm: LLMBase
  llmPlaner: LLMBase
  retry: number
  constructor(config: FlappyAgentConfig<TFunctions>) {
    this.config = config
    this.llm = config.llm
    this.llmPlaner = config.llmPlaner ?? config.llm
    this.retry = config.retry ?? DEFAULT_RETRY
  }

  /**
   * Get function definitions as a JSON Schema object array.
   */
  public functionsDefinitions(): object[] {
    return this.config.functions.map((fn: AnyFlappyFunction) => fn.callingSchema)
  }

  /**
   * Find function by name.
   */
  public findFunction<
    TName extends TNames,
    TFunction extends AnyFlappyFunction = FindFlappyFunction<TFunctions, TName>
  >(name: TName): TFunction {
    const fn = this.config.functions.find((fn: AnyFlappyFunction) => fn.define.name === name)
    if (!fn) throw new Error(`Function definition not found: ${name}`)
    return fn
  }

  /**
   * List all synthesized functions.
   */
  public synthesizedFunctions(): SynthesizedFunction[] {
    return this.config.functions.filter((fn: AnyFlappyFunction) => fn instanceof SynthesizedFunction)
  }

  /**
   * List all invoke functions.
   */
  public invokeFunctions(): InvokeFunction[] {
    return this.config.functions.filter((fn: AnyFlappyFunction) => fn instanceof InvokeFunction)
  }

  /**
   * Call a function by name.
   */
  public async callFunction<
    TName extends TNames,
    TFunction extends AnyFlappyFunction = FindFlappyFunction<TFunctions, TName>
  >(name: TName, args: Parameters<TFunction['call']>[1]): Promise<ReturnType<TFunction['call']>> {
    const fn = this.findFunction(name)
    return await fn.call(this, args)
  }

  public async callCodeInterpreter(prompt: string): Promise<any> {
    const config = (this.config as FlappyAgentConfig).codeInterpreter
    if (!config) throw new Error('Code interpreter is not enabled.')
    const originalRequestMessage: ChatMLMessage[] = [
      {
        role: 'system',
        content: `You are an AI that writes Python code using only the built-in library. After you supply the Python code, it will be run in a safe sandbox. The execution time is limited to 120 seconds. The task is to define a function named "main" that doesn't take any parameters. The output should be a JSON object: {"code": string}.
        Network access is ${config.enableNetwork ? 'enabled' : 'disabled'}`
      },
      {
        role: 'user',
        content: `${prompt}\n\noutput json:\n`
      }
    ]
    let requestMessage = originalRequestMessage
    let retry = this.retry
    let result: ChatMLResponse | undefined

    while (true) {
      try {
        if (retry !== this.retry) console.debug('Attempt retry: ', this.retry - retry)
        console.dir(requestMessage, { depth: null })
        result = await this.llm.chatComplete(requestMessage)
        const data = JSON.parse(result.data!)?.code?.replace(/\\n/g, '\n')
        if (!data) throw new Error('Invalid JSON response')
        if (!data.includes('def main():')) throw new Error('Function "main" not found')
        console.debug('Generated Code:\n', data)
        const execResult = await evalPythonCode(
          `${data}\nprint(main())`,
          config.enableNetwork ?? false,
          Object.entries(config.env ?? {}),
          config.cacheDir
        )
        if (execResult.stderr) throw new Error(execResult.stderr)
        console.debug('CodeInterpreter Output', execResult.stdout)
        return execResult.stdout
      } catch (err) {
        console.error(err)
        if (retry <= 0) throw new Error('Interrupted, function call failed. Please refer to the error message above.')

        retry -= 1
        // if the response came from chatComplete is failed, retry it directly.
        // Otherwise, update message for repairing
        if (result?.success && result.data) {
          requestMessage = [
            ...originalRequestMessage,
            {
              role: 'assistant',
              content: result?.data ?? ''
            },
            {
              role: 'user',
              content: `You response is invalid for the following reason:
          ${(err as Error).message}

          Please try again.`
            }
          ]
        }
      }
    }
  }

  /**
   * executePlan
   * @param prompt user input prompt
   * @param enableCot enable CoT to improve the plan quality, but it will be generally more tokens. Default is true.
   */
  public async executePlan(prompt: string, enableCot: boolean = true): Promise<any> {
    const functions = convertJsonToYaml(this.functionsDefinitions())
    const zodSchema = lanOutputSchema(enableCot)
    const returnSchema = JSON.stringify(zodToCleanJsonSchema(zodSchema), null, 4)
    const originalRequestMessage: ChatMLMessage[] = [
      {
        role: 'system',
        content: `You are an AI assistant that makes step-by-step plans to solve problems, utilizing external functions. Each step entails one plan followed by a function-call, which will later be executed to gather args for that step.
        Make as few plans as possible if it can solve the problem.
        The functions list is described using the following YAML schema array:
        ${functions}

        Your specified plans should be output as JSON object array and adhere to the following JSON schema:
        ${returnSchema}

        Only the listed functions are allowed to be used.`
      },
      {
        role: 'user',
        content: `Prompt: ${prompt}\n\nPlan array:`
      }
    ]
    let requestMessage = originalRequestMessage
    let plan: any[] = []

    let retry = this.retry
    let result: ChatMLResponse | undefined

    while (true) {
      try {
        if (retry !== this.retry) console.debug('Attempt retry: ', this.retry - retry)
        console.dir(requestMessage, { depth: null })
        result = await this.llmPlaner.chatComplete(requestMessage)
        plan = this.parseComplete(result)

        // check for function calling in each step
        for (const step of plan) {
          const fn = this.findFunction(step.functionName)
          if (!fn) throw new Error(`Function definition not found: ${step.functionName}`)
        }

        break
      } catch (err) {
        console.error(err)
        if (retry <= 0) throw new Error('Interrupted, create plan failed. Please refer to the error message above.')

        retry -= 1
        // if the response came from chatComplete is failed, retry it directly.
        // Otherwise, update message for repairing
        if (result?.success && result.data) {
          requestMessage = [
            ...originalRequestMessage,
            {
              role: 'assistant',
              content: result?.data ?? ''
            },
            {
              role: 'user',
              content: `You response is invalid for the following reason:
          ${(err as Error).message}

          Please try again.`
            }
          ]
        }
      }
    }

    zodSchema.parse(plan)
    const returnStore = new Map()
    for (const step of plan) {
      const fn = this.findFunction<TNames>(step.functionName)
      const args = Object.fromEntries(
        Object.entries(step.args).map(([k, v]) => {
          if (typeof v === 'string' && v.startsWith(STEP_PREFIX)) {
            const keys = v.slice(STEP_PREFIX.length).split('.')
            const stepId = parseInt(keys[0]!, 10)
            const stepResult = returnStore.get(stepId)
            if (keys.length === 1) return [k, stepResult]

            // access object property
            return [k, keys.slice(1).reduce((acc, cur) => acc[cur], stepResult)]
          }
          return [k, v]
        })
      )
      console.debug('Start function call:', step.functionName)
      const result = await fn.call(this, args)
      console.debug('End Function call:', step.functionName)
      returnStore.set(step.id, result)
    }
    // step id starts from 1, so plan.length is the last step id
    // return the last step result
    return returnStore.get(plan.length)
  }

  protected parseComplete(resp: ChatMLResponse): any[] {
    const startIdx = resp.data!.indexOf('[')
    const endIdx = resp.data!.lastIndexOf(']')
    if (!(startIdx >= 0 && endIdx > startIdx)) throw new Error('Invalid JSON response')
    const json = JSON.parse(resp.data!.slice(startIdx, endIdx + 1))
    console.dir(json, { depth: null })
    return json
  }
}

/**
 * Create a flappy agent.
 * @param config
 * @returns
 */
export const createFlappyAgent = <const TFunctions extends AnyFlappyFunction[]>(
  config: FlappyAgentConfig<TFunctions>
): FlappyAgent<TFunctions> => new FlappyAgent(config)
