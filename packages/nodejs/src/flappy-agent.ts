import { type FlappyAgentInterface, type FlappyAgentConfig } from './flappy-agent.interface'
import { type LLMBase } from './llms/llm-base'
import { type ChatMLResponse, type ChatMLMessage } from './llms/interface'
import { STEP_PREFIX } from './flappy-agent.constants'
import { z } from './flappy-type'
import { convertJsonToYaml, zodToCleanJsonSchema, log } from './utils'
import { type FindFlappyFeature, type FlappyFeatureNames, type AnyFlappyFeature } from './flappy-feature'
import { type JsonObject } from 'roarr/dist/types'
import { templateRenderer } from './templates'

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
  TFeatures extends readonly AnyFlappyFeature[] = readonly AnyFlappyFeature[],
  TNames extends string = FlappyFeatureNames<TFeatures>
> implements FlappyAgentInterface
{
  config: FlappyAgentConfig<TFeatures>
  llm: LLMBase
  llmPlaner: LLMBase
  retry: number
  constructor(config: FlappyAgentConfig<TFeatures>) {
    this.config = config
    this.llm = config.llm
    this.llmPlaner = config.llmPlaner ?? config.llm
    this.retry = config.retry ?? DEFAULT_RETRY
  }

  /**
   * Get function definitions as a JSON Schema object array.
   */
  public featuresDefinitions(): object[] {
    return this.config.features.map((fn: AnyFlappyFeature) => fn.callingSchema)
  }

  /**
   * Find function by name.
   */
  public findFeature<TName extends TNames, TFunction extends AnyFlappyFeature = FindFlappyFeature<TFeatures, TName>>(
    name: TName
  ): TFunction {
    const fn = this.config.features.find((fn: AnyFlappyFeature) => fn.define.name === name)
    if (!fn) throw new Error(`Function definition not found: ${name}`)
    return fn as TFunction
  }

  /**
   * Call a feature by name.
   */
  public async callFunction<
    TName extends TNames,
    TFunction extends AnyFlappyFeature = FindFlappyFeature<TFeatures, TName>
  >(name: TName, args: Parameters<TFunction['call']>[1]): Promise<ReturnType<TFunction['call']>> {
    const fn = this.findFeature(name)
    // eslint-disable-next-line @typescript-eslint/return-await
    return await fn.call(this, args)
  }

  public executePlanSystemMessage(enableCot: boolean = true): ChatMLMessage {
    const functions = convertJsonToYaml(this.featuresDefinitions())
    const zodSchema = lanOutputSchema(enableCot)
    const returnSchema = JSON.stringify(zodToCleanJsonSchema(zodSchema), null, 4)
    return {
      role: 'system',
      content: `You are an AI assistant that makes step-by-step plans to solve problems, utilizing external functions. Each step entails one plan followed by a function-call, which will later be executed to gather args for that step.
        Make as few plans as possible if it can solve the problem.
        The functions list is described using the following YAML schema array:
        ${functions}

        Your specified plans should be output as JSON object array and adhere to the following JSON schema:
        ${returnSchema}

        Only the listed functions are allowed to be used.`
    }
  }

  /**
   * executePlan
   * @param prompt user input prompt
   * @param enableCot enable CoT to improve the plan quality, but it will be generally more tokens. Default is true.
   */
  public async executePlan(prompt: string, enableCot: boolean = true): Promise<any> {
    log.debug('Start planing')

    const zodSchema = lanOutputSchema(enableCot)
    const originalRequestMessage: ChatMLMessage[] = [
      this.executePlanSystemMessage(enableCot),
      { role: 'user', content: `Prompt: ${prompt}\n\nPlan array:` }
    ]
    let requestMessage = originalRequestMessage
    let plan: any[] = []

    let retry = this.retry
    let result: ChatMLResponse | undefined

    while (true) {
      try {
        if (retry !== this.retry) log.debug(`Attempt retry: ${this.retry - retry}`)

        log.debug({ data: requestMessage } as unknown as JsonObject, 'Submit the request message')

        result = await this.llmPlaner.chatComplete(requestMessage)
        plan = this.parseComplete(result)

        // check for function calling in each step
        for (const step of plan) {
          const fn = this.findFeature(step.functionName)
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
              content: templateRenderer('error/retry', { message: (err as Error).message })
            }
          ]
        }
      }
    }

    zodSchema.parse(plan)
    const returnStore = new Map()
    for (let i = 0; i < plan.length; i++) {
      const step = plan[i]
      const fn = this.findFeature<TNames>(step.functionName)
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
      log.debug(`Start step ${i + 1}`)
      log.debug(`Start function call: ${step.functionName}`)
      const result = await fn.call(this, args)
      log.debug(`End Function call: ${step.functionName}`)
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
    log.debug({ data: json }, 'The plan details')
    return json
  }
}

/**
 * Create a flappy agent.
 * @param config
 * @returns
 */
export const createFlappyAgent = <const TFeatures extends readonly AnyFlappyFeature[]>(
  config: FlappyAgentConfig<TFeatures>
): FlappyAgent<TFeatures> => new FlappyAgent(config)
