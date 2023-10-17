import { createFlappyAgent, createInvokeFunction, createSynthesizedFunction, z, ChatGPT } from '@pleisto/node-flappy'
import OpenAI from 'openai'

const gpt35 = new ChatGPT(
  new OpenAI({
    apiKey: process.env.OPENAI_API_KEY!,
    baseURL: process.env.OPENAI_API_BASE!
  }),
  'gpt-3.5-turbo'
)

enum Verdict {
  Innocent = 'Innocent',
  Guilty = 'Guilty',
  Unknown = 'Unknown'
}

const MOCK_LAWSUIT_DATA =
  "As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong."

const lawAgent = createFlappyAgent({
  llm: gpt35,
  functions: [
    createSynthesizedFunction({
      name: 'getMeta',
      description: 'Extract meta data from a lawsuit full text.',
      args: z.object({
        lawsuit: z.string().describe('Lawsuit full text.')
      }),
      returnType: z.object({
        verdict: z.nativeEnum(Verdict),
        plaintiff: z.string(),
        defendant: z.string(),
        judgeOptions: z.array(z.string())
      })
    }),
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
        return MOCK_LAWSUIT_DATA
      }
    })
  ]
})

void lawAgent.executePlan('Find the latest case with the plaintiff being families of victims and return its metadata.')
// void lawAgent.callFunction('getMeta', { lawsuit: MOCK_LAWSUIT_DATA }).then(console.log)
