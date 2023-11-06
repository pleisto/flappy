import * as isCI from './is-ci'
import * as generate from './generate'
import * as tsMustacheCodegen from './ts-mustache-codegen'

export const commands = [isCI, generate, tsMustacheCodegen]
