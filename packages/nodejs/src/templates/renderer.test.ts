import { expect, test, describe } from 'vitest'
import { templateRenderer } from './renderer'

describe('renderer', () => {
  test('ok', async () => {
    const result = await templateRenderer.render('test/ping', { name: 'foo' })
    expect(result).toEqual('foo pong')
  })
})
