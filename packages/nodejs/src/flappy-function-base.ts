import { type ZodType as z } from './flappy-type'
import { type FlappyFunctionDefinition } from './flappy-agent.interface'
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

export abstract class FlappyFunctionBase<
  TName extends string = string,
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> {
  define: FlappyFunctionDefinition<TName, TArgs, TReturn>
  callingSchema: object
  constructor(define: FlappyFunctionDefinition<TName, TArgs, TReturn>) {
    this.define = define
    this.callingSchema = buildJsonSchema(define)
  }

  public abstract call(agent: FlappyAgent, args: z.infer<TArgs>): z.infer<TReturn>
}
