import { type Writable, type z } from '../flappy-type'
import {
  type FeatureDefinitionBase,
  type FlappyFeatureMetadataBase,
  type CreateFunction
} from '../flappy-feature.interface'
import { FlappyFeatureBase } from './base'
import { type FlappyAgentInterface } from '..'

export const invokeFunctionType = 'invoke'

type ResolveFunction<in TArgs extends z.ZodType, TReturn extends z.ZodType> = (
  args: z.infer<TArgs>
) => Promise<z.infer<Writable<TReturn>>>

interface InvokeFunctionDefinitionDefinition<TName extends string, TArgs extends z.ZodType, TReturn extends z.ZodType>
  extends FeatureDefinitionBase<TName, TArgs, TReturn> {
  resolve: ResolveFunction<TArgs, TReturn>
}

interface InvokeFunctionDefinition<TName extends string, TArgs extends z.ZodType, TReturn extends z.ZodType>
  extends FlappyFeatureMetadataBase<TName, TArgs, TReturn> {
  TDefinition: InvokeFunctionDefinitionDefinition<TName, TArgs, TReturn>
}

declare module '../flappy-feature.interface' {
  interface FlappyFeatureDefinitions<TName, TArgs, TReturn> {
    [invokeFunctionType]: InvokeFunctionDefinition<TName, TArgs, TReturn>
  }
}

export class InvokeFunction<
  TName extends string,
  TArgs extends z.ZodType,
  TReturn extends z.ZodType
> extends FlappyFeatureBase<InvokeFunctionDefinition<TName, TArgs, TReturn>> {
  public override async call(_agent: FlappyAgentInterface, args: z.infer<TArgs>): Promise<z.infer<TReturn>> {
    // eslint-disable-next-line @typescript-eslint/await-thenable, @typescript-eslint/return-await
    return await this.define.resolve(args)
  }
}

export const createInvokeFunction: CreateFunction<typeof invokeFunctionType> = (...args) =>
  new InvokeFunction(invokeFunctionType, ...args)
