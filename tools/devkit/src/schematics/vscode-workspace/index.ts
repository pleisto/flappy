import {
  type Rule,
  url,
  apply,
  applyTemplates,
  chain,
  mergeWith,
  MergeStrategy,
  move
} from '@angular-devkit/schematics'
import { readFileSync } from 'node:fs'
import { spawnSync } from 'node:child_process'
import { jsonc } from 'jsonc'

export function workspace(): Rule {
  const currentWorkspace = getCurrentCodeWorkspace()
  currentWorkspace.folders = currentYarnWorkspaces().map(workspace => ({
    name: workspace.name,
    path: workspace.location
  }))
  return chain([
    () =>
      mergeWith(
        apply(url('./files'), [
          applyTemplates({
            result: jsonc.stringify(currentWorkspace, {
              space: 2
            })
          }),
          move('.')
        ]),
        MergeStrategy.Overwrite
      )
  ])
}

/**
 * Return all yarn workspaces
 */
function currentYarnWorkspaces(): any[] {
  const yarnProcess = spawnSync('yarn', ['workspaces', 'list', '--json'])
  return yarnProcess.stdout
    .toString()
    .split(/\r?\n/)
    .filter(obj => obj.startsWith('{'))
    .map(obj => {
      return JSON.parse(obj)
    })
}

/**
 * Get current code-workspace for cwd
 */
function getCurrentCodeWorkspace(): any {
  try {
    const workspaceJson = readFileSync('pleisto.code-workspace', 'utf8')
    return jsonc.parse(workspaceJson)
  } catch {
    return {}
  }
}
