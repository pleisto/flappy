import { DefaultLoader, Renderer } from 'ts-mustache'
import { type TemplateMap } from './mustacheTypes'

const TemplateFolderPath = '../../templates'

const loader = new DefaultLoader({ dir: TemplateFolderPath })

export const templateRenderer = new Renderer<TemplateMap>(loader as any)
