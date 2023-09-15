import type { CommandModule } from 'yargs'
import chalk from 'chalk'
import { chdir } from 'node:process'
import { main } from '@angular-devkit/schematics-cli/bin/schematics'
import { monoRepoRootPath } from '../utils'
const defaultCollection = '@pleisto/flappy-devkit'
/**
 * Show usage information.
 */
const showUsage = (): void => {
  console.log(`yarn dev generate [collection-name:]<schematic-name> [options]

Common Options:
  --debug           Debug mode. This is true by default if the collection is a relative path.
  --allow-private   Allow private schematics to be run. This is false by default.
  --dry-run         Do not actually execute any effects. Default to true if debug mode is true.
  --force           Force overwriting files that would otherwise be an error.
  --no-interactive  Do not prompt for input.
  --verbose         Show more output.
  --help            Show help.

Available schematics in ${defaultCollection} collection:`)

  // list schematics
  void main({ args: [`${defaultCollection}:`, '--list-schematics'] })
  console.log(`
By default, if the collection-name is not specified, use the internal coolection provided by the ${defaultCollection}.
e.g. "${chalk.bold('yarn dev g vscode-workspace')}" equals "${chalk.bold(
    `yarn dev g ${defaultCollection}:vscode-workspace`
  )}".
`)
}

export const command = 'generate'
export const aliases = ['g']
export const describe = 'Generates and/or modifies files based on schematic.'

export const builder: CommandModule['builder'] = yargs =>
  // Skip checking for unrecognized commands that is will be handled by schematics-cli
  yargs.strictCommands(false)

export const handler: CommandModule['handler'] = (argv): void => {
  // Ignore the command name `generate`, and pass the rest of the arguments.
  const args = argv._.slice(1) as string[]

  // Show usage if no args
  if (args.length === 0) {
    showUsage()
    return
  }

  // Override default collection name if collection is not specified
  if (!args[0]?.includes(':')) args[0] = `${defaultCollection}:${args[0]}`

  // change cwd to monorepo root
  chdir(monoRepoRootPath)
  // Run schematic-cli
  void main({ args })
}
