import { type z } from 'zod'
import { type FlappyFunctionDefinition, type ResolveFunction } from './flappy-agent.interface'
import { type FlappyAgent } from './flappy-agent'
import { zodToCleanJsonSchema } from './utils'

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
const buildJsonSchema = (define: FlappyFunctionDefinition) => {
  const args = zodToCleanJsonSchema(define.args.describe('Function arguments'))
  const returnType = zodToCleanJsonSchema(define.returnType.describe('Function return type'))
  return {
    name: define.name,
    description: define.description,
    parameters: {
      type: 'object',
      properties: {
        args,
        returnType
      }
    }
  }
}

export abstract class FlappyFunctionBase<TArgs extends z.ZodType = z.ZodType, TReturn extends z.ZodType = z.ZodType> {
  define: FlappyFunctionDefinition<TArgs, TReturn, ResolveFunction<TArgs, TReturn>>
  callingSchema: object
  constructor(define: FlappyFunctionDefinition<TArgs, TReturn, ResolveFunction<TArgs, TReturn>>) {
    this.define = define
    this.callingSchema = buildJsonSchema(define)
  }

  public abstract call(agent: FlappyAgent, args: z.infer<TArgs>): z.infer<TReturn>
}
