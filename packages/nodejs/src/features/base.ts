import { type z } from 'zod'
import { type FlappyAgentInterface } from '..'
import {
  type FlappyFeatureDefinitions,
  type FlappyFeatureMetadataBase,
  type FlappyFeatureConstructorArguments,
  type FeatureKeys,
  type FlappyFeatureInterface
} from '../flappy-feature.interface'
import { zodToCleanJsonSchema } from '../utils'

type AnyFeatureDefinitions = FlappyFeatureDefinitions[FeatureKeys]

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
const buildJsonSchema = (that: FlappyFeatureBase<FlappyFeatureMetadataBase>) => {
  const define = that.define
  const args = zodToCleanJsonSchema(define.args.describe('Function arguments'))
  const returnType = zodToCleanJsonSchema(define.returnType.describe('Function return type'))
  return {
    name: define.name,
    description: that.buildDescription(),
    parameters: {
      type: 'object',
      properties: {
        args,
        returnType
      }
    }
  }
}

export abstract class FlappyFeatureBase<TMeta extends FlappyFeatureMetadataBase>
  implements FlappyFeatureInterface<TMeta>
{
  type: string
  define: FlappyFeatureInterface<TMeta>['define']
  callingSchema: object
  options?: TMeta['TOptions']
  constructor(type: string, ...[definition, options]: FlappyFeatureConstructorArguments<TMeta>) {
    this.type = type
    this.define = definition
    this.callingSchema = buildJsonSchema(this)
    this.options = options
  }

  public buildDescription(): string | undefined {
    return this.define.description
  }

  public abstract call(agent: FlappyAgentInterface, args: z.infer<TMeta['TArgs']>): Promise<z.infer<TMeta['TReturn']>>
}
