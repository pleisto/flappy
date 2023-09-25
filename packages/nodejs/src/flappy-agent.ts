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
import { ZodType as z } from './flappy-type'
import { zodToCleanJsonSchema } from './utils'

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
const lanOutputSchema = (enableCoT: boolean) => {
  const baseStep = {
    id: z.number().int().positive().describe('Increment id starting from 1'),
    functionName: z.string(),
    args: z
      .record(z.any())
      .describe(
        `an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '${STEP_PREFIX}' + the ID of the previous step (e.g. '${STEP_PREFIX}1'). If an 'returnType' in **previous** step's function's json schema is object, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema.`
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

  /**
   * executePlan
   * @param prompt user input prompt
   * @param enableCot enable CoT to improve the plan quality, but it will be generally more tokens. Default is true.
   */
  public async executePlan(prompt: string, enableCot: boolean = true): Promise<any> {
    const functions = JSON.stringify(this.functionsDefinitions())
    const zodSchema = lanOutputSchema(enableCot)
    const returnSchema = JSON.stringify(zodToCleanJsonSchema(zodSchema))
    const requestMessage: ChatMLMessage[] = [
      {
        role: 'system',
        content: `You are an AI assistant that makes step-by-step plans to solve problems, utilizing external functions. Each step entails one plan followed by a function-call, which will later be executed to gather args for that step.
        Make as few plans as possible if it can solve the problem.
        The functions list is described using the following JSON schema array:
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
    const result = await this.llmPlaner.chatComplete(requestMessage)
    const plan = this.parseComplete(result)
    zodSchema.parse(plan)
    const returnStore = new Map()
    for (const step of plan) {
      const fn = this.findFunction<TNames>(step.functionName)
      if (!fn) throw new Error(`Function definition not found: ${step.functionName}`)
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
