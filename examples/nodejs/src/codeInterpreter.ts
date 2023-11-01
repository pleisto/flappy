import { createFlappyAgent, ChatGPT, createCodeInterpreterFunction } from '@pleisto/node-flappy'
import OpenAI from 'openai'

const gpt35 = new ChatGPT(
  new OpenAI({
    apiKey: process.env.OPENAI_API_KEY!,
    baseURL: process.env.OPENAI_API_BASE!
  }),
  'gpt-3.5-turbo'
)

const agent = createFlappyAgent({
  llm: gpt35,
  features: [createCodeInterpreterFunction({ name: 'pythonSandbox' }, { enableNetwork: false })]
})

void agent.executePlan(
  'There are some rabbits and chickens in a barn. What is the number of chickens if there are 396 legs and 150 heads in the barn?'
)

// void agent.callFunction('pythonSandbox', {
//   code: `There are some rabbits and chickens in a barn.
//   What is the number of chickens if there are 396 legs and 150 heads in the barn?`
// })
