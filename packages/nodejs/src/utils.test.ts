import { expect, test, describe } from 'vitest'
import { zodToCleanJsonSchema, convertJsonToYaml } from './utils'
import { z } from './flappy-type'

describe('zodToCleanJsonSchema', () => {
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
})

describe('convertJsonToYaml', () => {
  const jsonObject = {
    type: 'array',
    items: {
      type: 'object',
      properties: {
        thought: {
          type: 'string',
          description: 'The thought why this step is needed.'
        },
        id: {
          type: 'integer',
          exclusiveMinimum: 0,
          description: 'Increment id starting from 1'
        },
        functionName: {
          type: 'string'
        },
        args: {
          type: 'object',
          additionalProperties: {},
          description:
            "'returnType' is not an argument.an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '%@_' + the ID of the previous step (e.g. '%@_1'). If an 'returnType' in **previous** step's function's json schema is object, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema."
        }
      },
      required: ['thought', 'id', 'functionName', 'args'],
      additionalProperties: false
    },
    description: 'An array storing the steps.'
  }
  const yamlResult = `type: array
items:
  type: object
  properties:
    thought:
      type: string
      description: The thought why this step is needed.
    id:
      type: integer
      exclusiveMinimum: 0
      description: Increment id starting from 1
    functionName:
      type: string
    args:
      type: object
      additionalProperties: {}
      description: "'returnType' is not an argument.an object encapsulating all
        arguments for a function call. If an argument's value is derived from
        the return of a previous step, it should be as '%@_' + the ID of the
        previous step (e.g. '%@_1'). If an 'returnType' in **previous** step's
        function's json schema is object, '.' should be used to access its
        properties, else just use id with prefix. This approach should remain
        compatible with the 'args' attribute in the function's JSON schema."
  required:
    - thought
    - id
    - functionName
    - args
  additionalProperties: false
description: An array storing the steps.
`
  test('convert JSON string to YAML string', () => {
    expect(convertJsonToYaml(JSON.stringify(jsonObject))).toBe(yamlResult)
  })

  test('convert JSON object to YAML string', () => {
    expect(convertJsonToYaml(jsonObject)).toBe(yamlResult)
  })
})
