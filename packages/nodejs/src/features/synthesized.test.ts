import { expect, test } from 'vitest'
import { z } from '../flappy-type'
import { createSynthesizedFunction } from './synthesized'

test('create SynthesizedFunction normally', () => {
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
