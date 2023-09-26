import { createFlappyAgent, ChatGPT, createInvokeFunction, zod } from '@pleisto/node-flappy'
import OpenAI from 'openai'

const gpt35 = new ChatGPT(
  new OpenAI({
    apiKey: process.env.OPENAI_API_KEY!,
    baseURL: process.env.OPENAI_API_BASE!
  }),
  'gpt-3.5-turbo'
)

export const agent = createFlappyAgent({
  llm: gpt35,
  functions: [
    createInvokeFunction({
      name: 'getLatestLawsuitsByPlaintiff',
      description: 'Get the latest lawsuits by plaintiff.',
      args: z.object({
        plaintiff: z.string(),
        arg1: z.boolean().describe('For demo purpose. set to False'),
        arg2: z.array(z.string()).describe('ignore it').optional()
      }),
      returnType: z.string(),
      resolve: async args => {
        // Do something
        // e.g. query SQL database
        console.debug('getLatestLawsuitsByPlaintiff called', args)
        return ''
      }
    })
  ]
})
