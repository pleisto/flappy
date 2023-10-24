import { expect, test } from 'vitest'
import { zodToCleanJsonSchema } from './utils'
import { z } from './flappy-type'

test('convert Zod schema to JSON schema', () => {
  const properties = ['prop1', 'prop2', 'prop3']

  const schema = z.object(Object.fromEntries(properties.map(p => [p, z.string()])))
  const json = zodToCleanJsonSchema(schema)

  properties.forEach(prop => expect(json.properties).toHaveProperty(prop))
})

test('remove unused properties', () => {
  const omittedProperties = ['$schema', 'additionalProperties']

  const schema = z.object({})
  const json = zodToCleanJsonSchema(schema)

  omittedProperties.forEach(prop => expect(json).not.toHaveProperty(prop))
})
