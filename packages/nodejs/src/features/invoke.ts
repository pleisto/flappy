import { type Writable, type z } from '../flappy-type'
import {
  type FeatureDefinitionBase,
  type FlappyFeatureMetadataBase,
  type CreateFunction
} from '../flappy-feature.interface'
import { FlappyFeatureBase } from './base'
import { type FlappyAgentInterface } from '..'

export const invokeFeatureType = 'invoke'

type ResolveFunction<in TArgs extends z.ZodType, TReturn extends z.ZodType> = (
  args: z.infer<TArgs>
) => Promise<z.infer<Writable<TReturn>>>

interface InvokeFeatureFunctionDefinition<TName extends string, TArgs extends z.ZodType, TReturn extends z.ZodType>
  extends FeatureDefinitionBase<TName, TArgs, TReturn> {
  resolve: ResolveFunction<TArgs, TReturn>
}

export interface InvokeFeatureDefinition<TName extends string, TArgs extends z.ZodType, TReturn extends z.ZodType>
  extends FlappyFeatureMetadataBase<TName, TArgs, TReturn> {
  TDefinition: InvokeFeatureFunctionDefinition<TName, TArgs, TReturn>
}

declare module '../flappy-feature.interface' {
  interface FlappyFeatureDefinitions<TName, TArgs, TReturn> {
    [invokeFeatureType]: InvokeFeatureDefinition<TName, TArgs, TReturn>
  }
}

export class InvokeFeature<
  TName extends string,
  TArgs extends z.ZodType,
  TReturn extends z.ZodType
> extends FlappyFeatureBase<InvokeFeatureDefinition<TName, TArgs, TReturn>> {
  public override async call(_agent: FlappyAgentInterface, args: z.infer<TArgs>): Promise<z.infer<TReturn>> {
    // eslint-disable-next-line @typescript-eslint/await-thenable, @typescript-eslint/return-await
    return await this.define.resolve(args)
  }
}

export const createInvokeFunction: CreateFunction<typeof invokeFeatureType> = (...args) =>
  new InvokeFeature(invokeFeatureType, ...args)
