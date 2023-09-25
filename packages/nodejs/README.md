# flappy Node.JS version

This package is about the Node.js version of the implementation which is written in TypeScript.

## Usage

### Installation

```bash
yarn add @pleisto/node-flappy
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
  }),
  'gpt-3.5-turbo'
)

const exampleAgent = createFlappyAgent({
  llm: gpt35,
  functions: []
})
```

#### Create a Synthesized Function

A synthesized function allows developers to format natural language using configuration fields for the LLM.

So far, `node-flappy` defines fields through [Zod](https://github.com/colinhacks/zod). Developers don't need to install `Zod` separately, `node-flappy` will re-export the necessary `Zod` methods and types to ensure version consistency.

With the help of `Zod`, we can define types for fields and provide descriptions (which will assist the LLM in understanding the meaning of the fields).

```ts
import { createFlappyAgent, createSynthesizedFunction, ZodType as z } from '@pleisto/node-agent'

//  ...

const exampleAgent = createFlappyAgent({
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
    })
  ]
})
```

#### create a Invoke Function

In addition to synthesized functions, developers can also add custom methods for the agent to invoke by including `invokeFunction`.

```ts
import { createFlappyAgent, createInvokeFunction, ZodType as z } from '@pleisto/flappy-agent'

// ...

const MOCK_LAWSUIT_DATA =
  "As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong."

const exampleAgent = createFlappyAgent({
  llm: gpt35,
  functions: [
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
      resolve: async (args: any): Promise<string> => {
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

## Interface

### FlappyAgent

The configuration used to build the FlappyAgent.

```ts
interface FlappyAgentConfig {
  /**
   * Which language model to use for inference.
   */
  llm: LLMBase

  /**
   * Which language model to use for planning.
   * If not specified, use `llm` instead.
   */
  llmPlaner?: LLMBase

  /**
   * Code interpreter could be used to execute code snippets in sandbox which are generated by language model.
   * If not specified, this feature will be disabled.
   */
  codeInterpreter?: 'python' | 'typescript'

  /**
   * List of functions that can be called by the agent or language model.
   */
  functions: FlappyFunction[]

  /**
   * Maximum number of retries when language model generation failed.
   * The default retries is 1.
   */
  retry?: number
}
```

Create a new `FlappyAgent` instance with `FlappyAgentConfig`.

```ts
function createFlappyAgent(config: FlappyAgentConfig): FlappyAgent
```

`FlappyAgent` class

```ts
class FlappyAgent {
  constructor(config: FlappyAgentConfig)

  /**
   * Get function definitions as a JSON Schema object array.
   */
  public functionsDefinitions(): object[]

  /**
   * Find function by name.
   */
  public findFunction(name: string): FlappyFunction | undefined

  /**
   * List all synthesized functions.
   */
  public synthesizedFunctions(): SynthesizedFunction[]

  /**
   * List all invoke functions.
   */
  public invokeFunctions(): InvokeFunction[]

  /**
   * Call a function by name.
   */
  public async callFunction(name: string, args: any): Promise<any>

  /**
   * executePlan
   * @param prompt user input prompt
   * @param enableCot enable CoT to improve the plan quality, but it will be generally more tokens. Default is true.
   */
  public async executePlan(prompt: string, enableCot: boolean = true): Promise<any>
}
```

### FunctionsDefinitionBase

The base function definition interface.

```ts
interface FunctionsDefinitionBase<TArgs extends z.ZodType, TReturn extends z.ZodType> {
  name: string
  description?: string
  args: TArgs
  returnType: TReturn
}
```

### SynthesizedFunction

The `SynthesizedFunction` definition used to create the function

```ts
interface SynthesizedFunctionDefinition<TArgs extends z.ZodType, TReturn extends z.ZodType>
  extends FunctionsDefinitionBase<TArgs, TReturn> {}
```

Create a `SynthesizedFunction`

```ts
function createSynthesizedFunction<TArgs extends z.ZodType = z.ZodType, TReturn extends z.ZodType = z.ZodType>(
  define: SynthesizedFunctionDefinition<TArgs, TReturn>
): SynthesizedFunction<TArgs, TReturn>
```

`SynthesizedFunction` class

```ts
class SynthesizedFunction<
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> extends FlappyFunctionBase {
  /**
   *  call the SynthesizedFunction
   **/
  public async call(agent: FlappyAgent, args: z.infer<TArgs>): Promise<z.infer<TReturn>>
}
```

### InvokeFunction

The `InvokeFunction` definition used to create the function

```ts
interface InvokeFunctionDefinition<
  TArgs extends z.ZodType,
  TReturn extends z.ZodType,
  TResolve extends ResolveFunction<TArgs, TReturn>
> extends FunctionsDefinitionBase<TArgs, TReturn> {
  resolve: TResolve
}
```

Create an `InvokeFunction`

```ts
/**
 * Create an invoke function.
 * @param define
 * @returns
 */
export const createInvokeFunction = <TArgs extends z.ZodType = z.ZodType, TReturn extends z.ZodType = z.ZodType>(
  define: InvokeFunctionDefinition<TArgs, TReturn, ResolveFunction<TArgs, TReturn>>
): InvokeFunction<TArgs, TReturn>
```

`InvokeFunction` class

```ts
class InvokeFunction<
  TArgs extends z.ZodType = z.ZodType,
  TReturn extends z.ZodType = z.ZodType
> extends FlappyFunctionBase {
  /**
   *  call the InvokeFunction
   **/
  public async call(_agent: FlappyAgent, args: z.infer<TArgs>): Promise<z.infer<TReturn>>
}
```

### ZodType

For more information about `ZodType`, please refer to https://github.com/colinhacks/zod
