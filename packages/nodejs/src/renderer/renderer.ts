import { type TemplateMap } from './mustacheTypes'
import Mustache from 'mustache'
import { templates } from './templates'

export type TemplateRenderer = <K extends keyof TemplateMap>(templateName: K, params: TemplateMap[K]) => string

export type RendererType = {
  [K in keyof TemplateMap]: (params: TemplateMap[K]) => string
}

export const templateRenderer: TemplateRenderer = (name, params) => {
  const template = templates[name]
  if (!template) throw new Error(`Unknown template '${String(name)}'`)

  return Mustache.render(template, params, undefined, { escape: value => value })
}
