import { zodToJsonSchema } from 'zod-to-json-schema'
import { omit } from 'radash'
import { type ZodSchema } from 'zod'

/**
 * Convert Zod schema to JSON schema and remove unused properties.
 * @param schema
 * @returns
 */
export const zodToCleanJsonSchema = (schema: ZodSchema<any>): any =>
  omit(zodToJsonSchema(schema) as any, ['$schema', 'additionalProperties'])
