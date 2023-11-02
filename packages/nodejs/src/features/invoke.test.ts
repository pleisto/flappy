import { expect, test } from 'vitest'
import { z } from '../flappy-type'
import { createInvokeFunction } from './invoke'

test('create InvokeFunction normally', async () => {
  const synthesizedFunction = createInvokeFunction({
    name: 'getFrontendEngineerResumes',
    description: 'Get all frontend engineer resumes.',
    args: z.string(),
    returnType: z.array(z.string()),
    resolve: async foo => {
      return [foo.concat('bar')]
    }
  })

  const result = await synthesizedFunction.call(null as any, 'foo')

  expect(result).toEqual(['foobar'])
})
