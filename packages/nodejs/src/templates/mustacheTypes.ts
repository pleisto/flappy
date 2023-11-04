type MustacheValue = string | number | boolean

type MustacheRecord<T> = T

type MustacheSection<T> = T[] | T

interface Test_ping {
  name: MustacheValue
}

interface Error_retry {
  message: MustacheValue
}

interface Agent_system {
  functions: MustacheValue
  returnSchema: MustacheValue
}

export type TemplateMap = {
  'test/ping': Test_ping,
  'error/retry': Error_retry,
  'agent/system': Agent_system,
}