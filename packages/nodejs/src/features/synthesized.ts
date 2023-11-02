import { log } from '../utils'
import { type z } from '../flappy-type'
import { type ChatMLResponse, type ChatMLMessage } from '../llms/interface'
import { omit } from 'radash'
import { type JsonValue } from 'roarr/dist/types'
import { type FlappyFeatureMetadataBase, type CreateFunction } from '../flappy-feature.interface'
import { FlappyFeatureBase } from './base'
import { type FlappyAgentInterface } from '..'

const extractSchema = (schema: any, prop: string): string =>
  JSON.stringify(omit(schema.parameters.properties[prop], ['description']))

export const synthesizedFunctionType = 'synthesized'

interface SynthesizedFunctionDefinition<TName extends string, TArgs extends z.ZodType, TReturn extends z.ZodType>
  extends FlappyFeatureMetadataBase<TName, TArgs, TReturn> {}

declare module '../flappy-feature.interface' {
  interface FlappyFeatureDefinitions<TName, TArgs, TReturn> {
    [synthesizedFunctionType]: SynthesizedFunctionDefinition<TName, TArgs, TReturn>
  }
}

export class SynthesizedFunction<
  TName extends string,
  TArgs extends z.ZodType,
  TReturn extends z.ZodType
> extends FlappyFeatureBase<SynthesizedFunctionDefinition<TName, TArgs, TReturn>> {
  public override async call(agent: FlappyAgentInterface, args: z.infer<TArgs>): Promise<z.infer<TReturn>> {
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
    let retry = this.options?.retry ?? agent.retry
    let result: ChatMLResponse | undefined

    while (true) {
      try {
        if (retry !== agent.retry) log.debug(`Attempt retry: ${agent.retry - retry}`)
        log.debug({ data: requestMessage as unknown as JsonValue }, 'Submit the request message')
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

  private parseComplete(resp: ChatMLResponse): string {
    const startIdx = resp.data!.indexOf('{')
    const endIdx = resp.data!.lastIndexOf('}')
    if (!(startIdx >= 0 && endIdx > startIdx)) throw new Error('Invalid JSON response')
    const json = JSON.parse(resp.data!.slice(startIdx, endIdx + 1))
    this.define.returnType.parse(json)
    log.debug({ data: json }, 'The synthesized result')
    return json
  }
}

export const createSynthesizedFunction: CreateFunction<typeof synthesizedFunctionType> = (...args) =>
  new SynthesizedFunction(synthesizedFunctionType, ...args)
