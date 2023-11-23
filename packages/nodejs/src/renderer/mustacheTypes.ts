type MustacheValue = string | number | boolean

interface Test_ping {
  name: MustacheValue
}

interface Error_retry {
  message: MustacheValue
}

interface Agent_userMessage {
  prompt: MustacheValue
}

interface Agent_systemMessage {
  functions: MustacheValue
  returnSchema: MustacheValue
}

interface Features_synthesized_userMessage {
  prompt: MustacheValue
}

interface Features_synthesized_systemMessage {
  describe: MustacheValue
  argsSchema: MustacheValue
  returnTypeSchema: MustacheValue
}

interface Features_codeInterpreter_evalCode {
  code: MustacheValue
}

interface Features_codeInterpreter_description {
  enabled?: MustacheValue
}

export type TemplateMap = {
  'test/ping': Test_ping,
  'error/retry': Error_retry,
  'agent/userMessage': Agent_userMessage,
  'agent/systemMessage': Agent_systemMessage,
  'features/synthesized/userMessage': Features_synthesized_userMessage,
  'features/synthesized/systemMessage': Features_synthesized_systemMessage,
  'features/codeInterpreter/evalCode': Features_codeInterpreter_evalCode,
  'features/codeInterpreter/description': Features_codeInterpreter_description,
}

export type TemplateName = keyof TemplateMap

export const TEMPLATES = [
  'test/ping',
  'error/retry',
  'agent/userMessage',
  'agent/systemMessage',
  'features/synthesized/userMessage',
  'features/synthesized/systemMessage',
  'features/codeInterpreter/evalCode',
  'features/codeInterpreter/description',
] as const