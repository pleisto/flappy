import { type FlappyAgentInterface, type Constructor, type z } from '.'

export interface FlappyFeatureOptions {
  /**
   * Maximum number of retries when function calling failed.
   * The default retries is 1.
   * If the value doesn't set, it would use the agent.retry instead.
   * Currently, it works on SynthesizedFunction only.
   */
  retry?: number
}

export interface FlappyFeatureDefinitions<
  TName extends string = string,
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> {}

export type FeatureKeys = keyof FlappyFeatureDefinitions

export interface FeatureDefinitionBase<TName extends string, TArgs extends z.ZodType, TReturn extends z.ZodType> {
  name: TName
  description?: string
  args: TArgs
  returnType: TReturn
}

export interface FlappyFeatureMetadataBase<
  TName extends string = string,
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> {
  TName: TName
  TArgs: TArgs
  TReturn: TReturn
  TDefinition: FeatureDefinitionBase<TName, TArgs, TReturn>
  TOptions: FlappyFeatureOptions
}

export type FlappyFeatureConstructorArguments<TMeta extends FlappyFeatureMetadataBase> = [
  definition: TMeta['TDefinition'],
  options?: TMeta['TOptions']
]

export type AllFlappyFeaturesInstance<
  TName extends string = string,
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> = {
  [K in FeatureKeys]: FlappyFeatureInterface<FlappyFeatureDefinitions<TName, TArgs, TReturn>[K]>
}

export type AllFlappyFeaturesClass = {
  [K in FeatureKeys]: Constructor<AllFlappyFeaturesInstance[K]>
}

export type CreateFunction<TType extends keyof FlappyFeatureDefinitions> = <
  TName extends string,
  TArgs extends z.ZodType,
  TReturn extends z.ZodType
>(
  ...args: FlappyFeatureConstructorArguments<FlappyFeatureDefinitions<TName, TArgs, TReturn>[TType]>
) => AllFlappyFeaturesInstance<TName, TArgs, TReturn>[TType]

export interface FlappyFeatureInterface<TMeta extends FlappyFeatureMetadataBase> {
  define: TMeta['TDefinition']
  callingSchema: object

  buildDescription: () => string | undefined
  call: (agent: FlappyAgentInterface, args: z.infer<TMeta['TArgs']>) => Promise<z.infer<TMeta['TReturn']>>
}
