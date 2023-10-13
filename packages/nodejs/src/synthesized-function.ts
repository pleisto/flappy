import { type z } from './flappy-type'
import { type SynthesizedFunctionDefinition } from './flappy-agent.interface'
import { type FlappyAgent } from './flappy-agent'
import { type ChatMLResponse, type ChatMLMessage } from './llm/interface'
import { FlappyFunctionBase, type FlappyFunctionOptions } from './flappy-function-base'
import { omit } from 'radash'

const extractSchema = (schema: any, prop: string): string =>
  JSON.stringify(omit(schema.parameters.properties[prop], ['description']))

export class SynthesizedFunction<
  TName extends string = string,
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> extends FlappyFunctionBase<TName, TArgs, TReturn> {
  declare define: SynthesizedFunctionDefinition<TName, TArgs, TReturn>

  public async call(
    agent: FlappyAgent,
    args: z.infer<TArgs>,
    options?: FlappyFunctionOptions
  ): Promise<z.infer<TReturn>> {
    const describe = this.define.description
    const returnTypeSchema = extractSchema(this.callingSchema, 'returnType')
    const argsSchema = extractSchema(this.callingSchema, 'args')
    const prompt = (args as any) instanceof Object ? JSON.stringify(args) : args
    const originalRequestMessage: ChatMLMessage[] = [
      {
        role: 'system',
        content: `${describe}
        User request according to the following JSON Schema:
        ${argsSchema}

        Translate it into JSON objects according to the following JSON Schema:
        ${returnTypeSchema}`
      },
      {
        role: 'user',
        content: `user request:${prompt}\n\njson object:`
      }
    ]
    let requestMessage = originalRequestMessage
    let retry = options?.retry ?? agent.retry
    let result: ChatMLResponse | undefined

    while (true) {
      try {
        if (retry !== agent.retry) console.debug('Attempt retry: ', agent.retry - retry)
        console.dir(requestMessage, { depth: null })
        result = await agent.llm.chatComplete(requestMessage)
        const data = this.parseComplete(result)
        return data
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

  protected parseComplete(resp: ChatMLResponse): string {
    const startIdx = resp.data!.indexOf('{')
    const endIdx = resp.data!.lastIndexOf('}')
    if (!(startIdx >= 0 && endIdx > startIdx)) throw new Error('Invalid JSON response')
    const json = JSON.parse(resp.data!.slice(startIdx, endIdx + 1))
    this.define.returnType.parse(json)
    console.debug(json)
    return json
  }
}

/**
 * Create an synthesized function.
 * @param define
 * @returns
 */
export const createSynthesizedFunction = <
  const TName extends string,
  const TArgs extends z.ZodType,
  const TReturn extends z.ZodType
>(
  define: SynthesizedFunctionDefinition<TName, TArgs, TReturn>
): SynthesizedFunction<TName, TArgs, TReturn> => new SynthesizedFunction(define)
