import { type ZodType as z } from './flappy-type'
import { type SynthesizedFunctionDefinition } from './flappy-agent.interface'
import { type FlappyAgent } from './flappy-agent'
import { type ChatMLResponse, type ChatMLMessage } from './llm/interface'
import { FlappyFunctionBase } from './flappy-function-base'
import { omit } from 'radash'

const extractSchema = (schema: any, prop: string): string =>
  JSON.stringify(omit(schema.parameters.properties[prop], ['description']))

export class SynthesizedFunction<
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> extends FlappyFunctionBase {
  declare define: SynthesizedFunctionDefinition<TArgs, TReturn>

  public async call(agent: FlappyAgent, args: z.infer<TArgs>): Promise<z.infer<TReturn>> {
    const describe = this.define.description
    const returnTypeSchema = extractSchema(this.callingSchema, 'returnType')
    const argsSchema = extractSchema(this.callingSchema, 'args')
    const prompt = (args as any) instanceof Object ? JSON.stringify(args) : args
    const requestMessage: ChatMLMessage[] = [
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
    console.dir(requestMessage, { depth: null })
    let result = await agent.llm.chatComplete(requestMessage)

    let retry = agent.retry

    while (true) {
      try {
        if (retry !== agent.retry) console.debug('Attempt retry: ', agent.retry - retry)
        const data = this.parseComplete(result)
        return data
      } catch (err) {
        console.error(err)
        retry -= 1
        // try to repair the result
        result = await agent.llm.chatComplete([
          ...requestMessage,
          {
            role: 'assistant',
            content: result.data!
          },
          {
            role: 'user',
            content: `You response is invalid for the following reason:
          ${(err as Error).message}

          Please try again.`
          }
        ])
      }

      if (retry <= 0) throw new Error('Interrupted, function call failed. Please refer to the error message above.')
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
export const createSynthesizedFunction = <TArgs extends z.ZodType = z.ZodType, TReturn extends z.ZodType = z.ZodType>(
  define: SynthesizedFunctionDefinition<TArgs, TReturn>
): SynthesizedFunction<TArgs, TReturn> => new SynthesizedFunction(define)
