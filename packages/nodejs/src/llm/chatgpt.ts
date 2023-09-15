import { LLMBase } from './llm-base'
import { type ChatMLMessage, type ChatMLResponse, type GenerateConfig } from './interface'
import type OpenAI from 'openai'

/**
 * Calculate the default max tokens for a given model.
 * @see https://platform.openai.com/docs/models/overview
 * @param model Model name.
 * @returns
 */
const calcDefaultMaxTokens = (model: string): number => {
  if (model.includes('16k')) return 16385
  if (model.includes('32k')) return 32768
  if (model.includes('gpt-4')) return 8192
  return 4096
}

export class ChatGPT extends LLMBase {
  client: OpenAI
  maxTokens: number
  model: string
  constructor(client: OpenAI, model: string, maxTokens?: number) {
    super()
    this.client = client
    this.model = model
    this.maxTokens = maxTokens ?? calcDefaultMaxTokens(model)
  }

  async chatComplete(messages: ChatMLMessage[], config?: GenerateConfig): Promise<ChatMLResponse> {
    const resp = await this.client.chat.completions.create({
      model: this.model,
      messages,
      max_tokens: config?.maxTokens,
      ...config
    })
    const choice = resp.choices[0]
    if (!choice) return { success: false, data: undefined }
    return { success: true, data: choice.message.content! }
  }
}
