import chalk from 'chalk'
import { exit, env } from 'node:process'
import { type ChildProcess } from 'node:child_process'
import { URL } from 'node:url'
/**
 * @pleisto/flappy-devkit package root path.
 */
export const devkitRootPath = new URL('..', import.meta.url).pathname

/**
 * Monorepo root path.
 */
export const monoRepoRootPath = new URL('../../..', import.meta.url).pathname

/**
 * Styling error message string.
 * @param msg
 * @returns
 */
export const styledError = (msg: string): string => `${chalk.bold.red('ERROR:')} ${msg}`

/**
 * Styling warn message string.
 * @param msg
 * @returns
 */
export const styledWarn = (msg: string): string => `${chalk.bold.yellow('WARN:')} ${msg}`

/**
 * Exit on child process exit.
 * @param child
 */
export const exitOnChildExit = (child: ChildProcess): void => {
  child.on('exit', () => exit(child.exitCode ?? 0))
}
