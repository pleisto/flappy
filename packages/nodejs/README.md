# Flappy Node.js Version

This package is about the Node.js version of the implementation which is written in TypeScript.

<div align="center">

[![License](https://img.shields.io/github/license/pleisto/flappy.svg)](https://raw.githubusercontent.com/pleisto/flappy/main/LICENSE)
[![CI](https://img.shields.io/github/actions/workflow/status/pleisto/flappy/nodejs-ci.yml.svg)](https://github.com/pleisto/flappy/actions/workflows/nodejs-ci.yml)
[![NPM version](https://img.shields.io/npm/v/%40pleisto/node-flappy/next.svg)](https://npmjs.org/package/@pleisto/node-flappy)
[![codecov](https://codecov.io/gh/pleisto/flappy/graph/badge.svg?token=8C94YY3KBD&flag=nodejs)](https://codecov.io/gh/pleisto/flappy)

</div>

## Usage

### Installation

```bash
yarn add @pleisto/node-flappy@next
```

### Create a Plan

You can create a plan through `node-flappy` to offer your capabilities to the flappy agent. Afterwards, the flappy agent will create and execute the plan according to your requests.

#### Create an Agent

To create an agent, you'll need to provide an LLM (Large Language Model), along with the methods you want the agent to be able to use.

```ts
import { createFlappyAgent } from '@pleisto/node-agent'
import { ChatGPT } from '../llm/chatgpt'

const gpt35 = new ChatGPT(
  new OpenAI({
    apiKey: process.env.OPENAI_API_KEY!,
    baseURL: process.env.OPENAI_API_BASE!
  })
)

const exampleAgent = createFlappyAgent({
  llm: gpt35,
  features: []
})
```

#### Create a Synthesized Function

A synthesized function allows developers to format natural language using configuration fields for the LLM.

So far, `node-flappy` defines fields through [Zod](https://github.com/colinhacks/zod). Developers don't need to install `Zod` separately, `node-flappy` will re-export the necessary `Zod` methods and types to ensure version consistency.

With the help of `Zod`, we can define types for fields and provide descriptions (which will assist the LLM in understanding the meaning of the fields).

```ts
import { createFlappyAgent, createSynthesizedFunction, z } from '@pleisto/node-agent'

//  ...

const exampleAgent = createFlappyAgent({
  llm: gpt35,
  features: [
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
    })
  ]
})
```

#### Create an Invoke Function

In addition to synthesized functions, developers can also add custom methods for the agent to invoke by including `InvokeFunction`.

```ts
import { createFlappyAgent, createInvokeFunction, z } from '@pleisto/flappy-agent'

// ...

const MOCK_LAWSUIT_DATA =
  "As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong."

const exampleAgent = createFlappyAgent({
  llm: gpt35,
  features: [
    // ...
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
```

In real-world scenarios, developers can obtain the natural language text material that needs to be processed by interacting with a database or other sources. They can also define methods for any other functionalities, as long as it helps the agent achieve its objectives.

### Execute a Plan

Simply tell the agent your purpose.

```ts
lawAgent.executePlan('Find the latest case with the plaintiff being families of victims and return its metadata.')

// output
{
  verdict: 'Unknown',
  plaintiff: 'families of victims of Sandy Hook Elementary School shooting',
  defendant: 'Alex Jones',
  judgeOptions: [
    'reduce personal expenses to a reasonable level',
    'bar from further waste of estate assets',
    'appoint a trustee to oversee spending',
    'dismiss the bankruptcy case'
  ]
}
```

You can try out this example by running `yarn node-example law`.

### ZodType

For more information about `ZodType`, please refer to https://github.com/colinhacks/zod
