import {
  type FlappyFeatureMetadataBase,
  type FlappyFeatureDefinitions,
  type FlappyFeatureInterface
} from './flappy-feature.interface'
import { type IsNever } from './flappy-type'

export type ValidFeatureKeys = {
  [K in keyof FlappyFeatureDefinitions]: FlappyFeatureDefinitions[K] extends FlappyFeatureMetadataBase ? K : never
}[keyof FlappyFeatureDefinitions]

// export type AnyFlappyFeature = {
//   [K in ValidFeatureKeys]: FlappyFeatureInterface<FlappyFeatureDefinitions<string, z.ZodTypeAny, z.ZodTypeAny>[K]>
// }[ValidFeatureKeys]

export type AnyFlappyFeature = FlappyFeatureInterface<any>

export type FindFlappyFeature<
  TFeatures extends readonly AnyFlappyFeature[],
  Name extends string
> = IsNever<Name> extends true
  ? never
  : string extends Name
  ? never
  : TFeatures extends readonly [infer First extends AnyFlappyFeature, ...infer Rest extends readonly AnyFlappyFeature[]]
  ? First['define']['name'] extends Name
    ? First
    : FindFlappyFeature<Rest, Name>
  : never

export type FlappyFeatureNames<TFeatures extends readonly AnyFlappyFeature[]> = TFeatures[number]['define']['name']
