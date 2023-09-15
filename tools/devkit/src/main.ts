import 'v8-compile-cache'
import yargs, { type CommandModule } from 'yargs'
import { hideBin } from 'yargs/helpers'
import { styledError } from './utils'
import { commands } from './commands/mod'

/**
 * yargs entrypoint.
 */
void yargs(hideBin(process.argv))
  .scriptName('yarn dev')
  .usage('$0 <command> [args]')
  .command(commands as unknown as CommandModule<Record<string, unknown>, unknown>)
  .help() // append the help command
  .strictCommands() // unrecognized commands and args will throw an error
  .recommendCommands() // suggest commands for those that are not found
  .demandCommand(1, styledError('Command name argument expected.'))
  .wrap(120) // line wrap at 120 characters
  .parse()
