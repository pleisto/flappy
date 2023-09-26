import { useMemo } from 'react'
import { agent } from './flappyAgent'

export function useFlappyAgent() {
  return useMemo(() => agent, [])
}
