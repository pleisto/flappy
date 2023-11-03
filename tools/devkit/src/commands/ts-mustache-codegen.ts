import { DefaultLoader, Declarer } from 'ts-mustache'
import { type CommandModule } from 'yargs'
import fs from 'fs'
import { monoRepoRootPath } from '../utils'
import { chdir, exit } from 'process'
import { isCI } from './is-ci'

export const command = 'ts-mustache-codegen'
export const describe = 'Generate typescript code for mustache templates.'
export const builder: CommandModule['builder'] = {}
export const handler: CommandModule['handler'] = async (): Promise<void> => {
  chdir(monoRepoRootPath)
  const loader = new DefaultLoader({
    dir: './templates'
  })

  const TargetPath = './packages/nodejs/src/templates/mustacheTypes.ts'

  // Generate typedefs
  const declarer = new Declarer(loader)
  const types = await declarer.declare()
  const data = fs.readFileSync(TargetPath).toString()

  if (isCI) {
    if (data !== types) exit(1)
    console.log('codegen validate ok')
  } else {
    fs.writeFileSync(TargetPath, types)
  }

  exit(0)
}
