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
import { templateRenderer } from '../templates'

export const codeInterpreterType = 'codeInterpreter'

const CodeInterpreterInputZ = z.object({ code: z.string() })
const CodeInterpreterOutputZ = z.object({ result: z.string() })
export const defaultCodeInterpreterName = 'pythonSandbox'

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

interface CodeInterpreterDefinition<TName extends string>
  extends FlappyFeatureMetadataBase<TName, typeof CodeInterpreterInputZ, typeof CodeInterpreterOutputZ> {
  TOptions: CodeInterpreterOptions
  TArgs: typeof CodeInterpreterInputZ
  TReturn: typeof CodeInterpreterOutputZ
}

declare module '../flappy-feature.interface' {
  interface FlappyFeatureDefinitions<TName, TArgs, TReturn> {
    [codeInterpreterType]: CodeInterpreterDefinition<TName>
  }
}

export class CodeInterpreter<
  TName extends string,
  TArgs extends typeof CodeInterpreterInputZ,
  TReturn extends typeof CodeInterpreterOutputZ
> extends FlappyFeatureBase<CodeInterpreterDefinition<TName>> {
  public override buildDescription(): string {
    return templateRenderer('features/codeInterpreter/description', { enabled: this.options?.enableNetwork })
  }

  public override async call(_agent: FlappyAgentInterface, args: z.infer<TArgs>): Promise<z.infer<TReturn>> {
    log.debug('Calling code interpreter')
    const code = args.code.trim()

    if (!code.includes('def main():')) throw new Error('Function "main" not found')

    log.debug({ code }, 'Generated Code:')

    const result = await evalPythonCode(
      templateRenderer('features/codeInterpreter/evalCode', { code }),
      this.options?.enableNetwork ?? false,
      Object.entries(this.options?.env ?? {}),
      codeInterpreterGlobalCacheDirectory
    )

    log.debug(`Exec result: ${JSON.stringify(result)}`)
    if (result.stderr) throw new Error(result.stderr)

    return { result: result.stdout }
  }
}

type CodeInterpreterCreater = CreateFunction<typeof codeInterpreterType>
// Ensure the function parameters only have two elements.
type CodeInterpreterFunctionParameters = Required<Parameters<CodeInterpreterCreater>>['length'] extends 2
  ? Parameters<CodeInterpreterCreater>
  : never

export const createCodeInterpreter = <TName extends string = typeof defaultCodeInterpreterName>(
  definition?: Partial<Pick<FeatureDefinitionBase<TName, any, any>, 'name'>>,
  options?: CodeInterpreterFunctionParameters[1]
): AllFlappyFeaturesInstance<
  TName,
  typeof CodeInterpreterInputZ,
  typeof CodeInterpreterOutputZ
>[typeof codeInterpreterType] =>
  new CodeInterpreter(
    codeInterpreterType,
    {
      name: (definition?.name ?? defaultCodeInterpreterName) as TName,
      args: CodeInterpreterInputZ,
      returnType: CodeInterpreterOutputZ
    },
    options
  )
