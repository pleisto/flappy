import { expect, test, vi } from 'vitest'
import { createCodeInterpreterFunction } from './codeInterpreter'
import { type evalPythonCode } from '@pleisto/flappy-nodejs-bindings'

vi.mock('@pleisto/flappy-nodejs-bindings', () => {
  const mockedEvalPythonCode: typeof evalPythonCode = async (code, network, envs, cachePath) => {
    return { stdout: code, stderr: '' }
  }

  return { evalPythonCode: mockedEvalPythonCode }
})

test('create codeInterpreter normally', async () => {
  const codeInterpreterFunction = createCodeInterpreterFunction({
    name: 'codeInterpreter'
  })

  const code = `def main():
  return "foo"
`

  expect(codeInterpreterFunction.buildDescription() ?? '').not.toEqual('')

  const result = await codeInterpreterFunction.call(null as any, { code })
  expect(result.result.startsWith(code)).toBe(true)

  await expect(async () => {
    await codeInterpreterFunction.call(null as any, { code: '' })
  }).rejects.toThrowError(`Function "main" not found`)
})
