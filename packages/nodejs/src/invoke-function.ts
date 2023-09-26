import { type z } from './flappy-type'
import { type InvokeFunctionDefinition } from './flappy-agent.interface'
import { type FlappyAgent } from './flappy-agent'

import { FlappyFunctionBase } from './flappy-function-base'

export class InvokeFunction<
  TName extends string = string,
  in out TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> extends FlappyFunctionBase<TName, TArgs, TReturn> {
  declare define: InvokeFunctionDefinition<TName, TArgs, TReturn>

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
export const createInvokeFunction = <
  const TName extends string,
  const TArgs extends z.ZodType,
  const TReturn extends z.ZodType
>(
  define: InvokeFunctionDefinition<TName, TArgs, TReturn>
): InvokeFunction<TName, TArgs, TReturn> => new InvokeFunction(define)
