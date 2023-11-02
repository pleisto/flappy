import { type CreateFunction, type AllFlappyFeaturesClass, type FeatureKeys } from '../flappy-feature.interface'
import { CodeInterpreter, codeInterpreterType } from './codeInterpreter'
import { InvokeFunction, invokeFunctionType } from './invoke'
import { SynthesizedFunction, synthesizedFunctionType } from './synthesized'

export const FlappyFeatures: AllFlappyFeaturesClass = {
  [invokeFunctionType]: InvokeFunction,
  [synthesizedFunctionType]: SynthesizedFunction,
  [codeInterpreterType]: CodeInterpreter
}

export const createFeatureBuilder =
  <const TType extends FeatureKeys>(type: TType): CreateFunction<TType> =>
  (...args) =>
    new FlappyFeatures[type](type, ...args) as any

export * from './base'

export * from './invoke'
export * from './synthesized'
export * from './codeInterpreter'
