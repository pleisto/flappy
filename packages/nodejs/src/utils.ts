import { zodToJsonSchema } from 'zod-to-json-schema'
import { omit } from 'radash'
import { type ZodSchema } from './flappy-type'
import { Document } from 'yaml'
import createSerializeErrorMiddleware from '@roarr/middleware-serialize-error'
import { Roarr } from 'roarr'

/**
 * Convert Zod schema to JSON schema and remove unused properties.
 * @param schema
 * @returns
 */
export const zodToCleanJsonSchema = (schema: ZodSchema<any>): any =>
  omit(zodToJsonSchema(schema) as any, ['$schema', 'additionalProperties'])

/**
 * Convert JSON string or JSON object to YAML string
 * @param json
 * @returns
 */
export const convertJsonToYaml = (json: string | object): string => {
  const doc = new Document()
  doc.contents = typeof json === 'string' ? JSON.parse(json) : json
  return doc.toString()
}

export const log = Roarr.child<{ error: Error }>(createSerializeErrorMiddleware())
