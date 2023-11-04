import { type TemplateMap } from './mustacheTypes'
import { globSync } from 'glob'
import path from 'path'
import fs from 'fs'
import Mustache from 'mustache'

const TemplateFolderPath = '../../templates'
const MUSTACHE_EXTENSION = '.mustache'

const files = globSync(path.join(TemplateFolderPath, `**/*${MUSTACHE_EXTENSION}`))

if (files.length === 0) throw new Error('template not found')

const contents = files.map(f => fs.readFileSync(f, { encoding: 'utf8' }))

const templates = Object.fromEntries(
  files.map((f, i) => [path.relative(TemplateFolderPath, f).replace(MUSTACHE_EXTENSION, ''), contents[i]])
)

console.log('templates', templates)

export type TemplateRenderer = <K extends keyof TemplateMap>(templateName: K, params: TemplateMap[K]) => string

export const templateRenderer: TemplateRenderer = (name, params) => {
  const template = templates[name]
  if (!template) throw new Error(`Unknown template '${String(name)}'`)

  return Mustache.render(template, params, undefined, { escape: value => value })
}
