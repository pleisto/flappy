import { type CreateFunction, type AllFlappyFeaturesClass, type FeatureKeys } from '../flappy-feature.interface'
import { CodeInterpreterFeature, codeInterpreterFeatureType } from './codeInterpreter'
import { InvokeFeature, invokeFeatureType } from './invoke'
import { SynthesizedFeature, synthesizedFeatureType } from './synthesized'

export const FlappyFeatures: AllFlappyFeaturesClass = {
  [invokeFeatureType]: InvokeFeature,
  [synthesizedFeatureType]: SynthesizedFeature,
  [codeInterpreterFeatureType]: CodeInterpreterFeature
}

export const createFeatureBuilder =
  <const TType extends FeatureKeys>(type: TType): CreateFunction<TType> =>
  (...args) =>
    new FlappyFeatures[type](type, ...args) as any

export * from './base'

export * from './invoke'
export * from './synthesized'
export * from './codeInterpreter'
