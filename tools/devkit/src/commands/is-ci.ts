/* eslint-disable @typescript-eslint/prefer-nullish-coalescing */
import { env, exit } from 'node:process'

/**
 * Returns true if the current process is running in a CI environment.
 */
const isCI = !!(
  env.CI || // Travis CI, CircleCI, AppVeyor, GitHub Actions
  env.CONTINUOUS_INTEGRATION || // Travis CI, CircleCI
  env.BUILD_NUMBER || // Jenkins, TeamCity
  env.RUN_ID || // TaskCluster
  false
)

export const command = 'is-ci'
export const describe = 'Check if we are running in a CI environment, exit code with 1 if not and 0 if so.'
export const builder = {}
export const handler = (): void => exit(isCI ? 0 : 1)
