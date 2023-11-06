import { expect, test, describe } from 'vitest'
import { templateRenderer } from './renderer'

describe('renderer', () => {
  test('ok', () => {
    const result = templateRenderer('test/ping', { name: 'foo' })
    expect(result).toEqual('foo pong')
  })
})
