import { expect, test } from 'vitest'
import { createInvokeFunction } from './invoke-function'
import { z } from './flappy-type'

test('create invokeFunction normally', () => {
  const define = {
    name: 'getFrontendEngineerResumes',
    description: 'Get all frontend engineer resumes.',
    args: z.null(),
    returnType: z.array(z.string()),
    resolve: async () => []
  }

  const synthesizedFunction = createInvokeFunction({
    name: 'getFrontendEngineerResumes',
    description: 'Get all frontend engineer resumes.',
    args: z.null(),
    returnType: z.array(z.string()),
    resolve: async () => []
  })

  expect(synthesizedFunction.define.name).toBe(define.name)
})
