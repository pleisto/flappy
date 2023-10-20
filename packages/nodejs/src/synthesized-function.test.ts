import { expect, test } from 'vitest'
import { createSynthesizedFunction } from './synthesized-function'
import { z } from './flappy-type'

test('create synthesizedFunction normally', () => {
  const define = {
    name: 'getMeta',
    description: 'Extract meta data from a lawsuit full text.',
    args: z.object({
      lawsuit: z.string().describe('Lawsuit full text.')
    }),
    returnType: z.object({
      plaintiff: z.string(),
      defendant: z.string(),
      judgeOptions: z.array(z.string())
    })
  }

  const synthesizedFunction = createSynthesizedFunction(define)

  expect(synthesizedFunction.define).toBe(define)
})
