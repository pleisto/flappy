import { z } from 'zod'
import { FlappyFeatureBase } from './base'
import { type FlappyAgentInterface } from '../flappy-agent.interface'
import {
  type FlappyFeatureOptions,
  type FlappyFeatureMetadataBase,
  type CreateFunction,
  type AllFlappyFeaturesInstance,
  type FeatureDefinitionBase
} from '../flappy-feature.interface'
import { evalPythonCode } from '@pleisto/flappy-nodejs-bindings'
import { log } from '../utils'

export const codeInterpreterFeatureType = 'codeInterpreter'

const CodeInterpreterInputZ = z.object({ code: z.string() })
const CodeInterpreterOutputZ = z.object({ result: z.string() })

const codeInterpreterGlobalCacheDirectory = 'tmp'

export interface CodeInterpreterOptions extends FlappyFeatureOptions {
  /**
   * Enable network access in sandbox. Default is false.
   */
  enableNetwork?: boolean

  /**
   * Environment variables for code interpreter.
   */
  env?: Record<string, string>
}

interface CodeInterpreterFeatureDefinition<TName extends string>
  extends FlappyFeatureMetadataBase<TName, typeof CodeInterpreterInputZ, typeof CodeInterpreterOutputZ> {
  TOptions: CodeInterpreterOptions
  TArgs: typeof CodeInterpreterInputZ
  TReturn: typeof CodeInterpreterOutputZ
}

declare module '../flappy-feature.interface' {
  interface FlappyFeatureDefinitions<TName, TArgs, TReturn> {
    [codeInterpreterFeatureType]: CodeInterpreterFeatureDefinition<TName>
  }
}

export class CodeInterpreterFeature<
  TName extends string,
  TArgs extends typeof CodeInterpreterInputZ,
  TReturn extends typeof CodeInterpreterOutputZ
> extends FlappyFeatureBase<CodeInterpreterFeatureDefinition<TName>> {
  public override buildDescription(): string {
    return `
      An safe sandbox that only support the built-in library. The execution time is limited to 120 seconds. The task is to define a function named "main" that doesn't take any parameters. The output should be a String. Network access is ${
        this.options?.enableNetwork ? 'enabled' : 'disabled'
      }
    `.trim()
  }

  public override async call(_agent: FlappyAgentInterface, args: z.infer<TArgs>): Promise<z.infer<TReturn>> {
    log.debug('Calling code interpreter')
    const code = args.code.trim()

    if (!code.includes('def main():')) throw new Error('Function "main" not found')

    log.debug({ code }, 'Generated Code:')

    const result = await evalPythonCode(
      `${code}\nprint(main())`,
      this.options?.enableNetwork ?? false,
      Object.entries(this.options?.env ?? {}),
      codeInterpreterGlobalCacheDirectory
    )

    log.debug(`Exec result: ${JSON.stringify(result)}`)
    if (result.stderr) throw new Error(result.stderr)

    return { result: result.stdout }
  }
}

type CodeInterpreterFunction = CreateFunction<typeof codeInterpreterFeatureType>
// Ensure the function parameters only have two elements.
type CodeInterpreterFunctionDefinition = Required<Parameters<CodeInterpreterFunction>>['length'] extends 2
  ? Parameters<CodeInterpreterFunction>
  : never

export const createCodeInterpreterFunction = <TName extends string>(
  definition: Pick<FeatureDefinitionBase<TName, any, any>, 'name'>,
  options?: CodeInterpreterFunctionDefinition[1]
): AllFlappyFeaturesInstance<
  TName,
  typeof CodeInterpreterInputZ,
  typeof CodeInterpreterOutputZ
>[typeof codeInterpreterFeatureType] =>
  new CodeInterpreterFeature(
    codeInterpreterFeatureType,
    { ...definition, args: CodeInterpreterInputZ, returnType: CodeInterpreterOutputZ },
    options
  )
