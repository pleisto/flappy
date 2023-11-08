import agentSystemMessage from '../../templates/agent/systemMessage.mustache?raw'
import agentUserMessage from '../../templates/agent/userMessage.mustache?raw'
import errorRetry from '../../templates/error/retry.mustache?raw'
import featuresCodeInterpreterDescription from '../../templates/features/codeInterpreter/description.mustache?raw'
import featuresCodeInterpreterEvalCode from '../../templates/features/codeInterpreter/evalCode.mustache?raw'
import featuresSynthesizedSystemMessage from '../../templates/features/synthesized/systemMessage.mustache?raw'
import featuresSynthesizedUserMessage from '../../templates/features/synthesized/userMessage.mustache?raw'
import testPing from '../../templates/test/ping.mustache?raw'
import { type TemplateMap } from './mustacheTypes'

export const templates: Record<keyof TemplateMap, string> = {
  'agent/systemMessage': agentSystemMessage,
  'agent/userMessage': agentUserMessage,
  'error/retry': errorRetry,
  'features/codeInterpreter/description': featuresCodeInterpreterDescription,
  'features/codeInterpreter/evalCode': featuresCodeInterpreterEvalCode,
  'features/synthesized/systemMessage': featuresSynthesizedSystemMessage,
  'features/synthesized/userMessage': featuresSynthesizedUserMessage,
  'test/ping': testPing
}
