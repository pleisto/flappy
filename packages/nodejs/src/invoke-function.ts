import { type z } from 'zod'
import { type InvokeFunctionDefinition, type ResolveFunction } from './flappy-agent.interface'
import { type FlappyAgent } from './flappy-agent'

import { FlappyFunctionBase } from './flappy-function-base'

export class InvokeFunction<
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> extends FlappyFunctionBase {
  declare define: InvokeFunctionDefinition<TArgs, TReturn, ResolveFunction<TArgs, TReturn>>

  public async call(_agent: FlappyAgent, args: z.infer<TArgs>): Promise<z.infer<TReturn>> {
    // eslint-disable-next-line @typescript-eslint/await-thenable, @typescript-eslint/return-await
    return await this.define.resolve(args)
  }
}

/**
 * Create an invoke function.
 * @param define
 * @returns
 */
export const createInvokeFunction = <TArgs extends z.ZodType = z.ZodType, TReturn extends z.ZodType = z.ZodType>(
  define: InvokeFunctionDefinition<TArgs, TReturn, ResolveFunction<TArgs, TReturn>>
): InvokeFunction<TArgs, TReturn> => new InvokeFunction(define)
