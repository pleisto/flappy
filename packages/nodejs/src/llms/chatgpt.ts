import { LLMBase } from './llm-base'
import { type ChatMLMessage, type ChatMLResponse, type GenerateConfig } from './interface'
import type OpenAI from 'openai'
import { log } from '../utils'
import {
  type ChatCompletionCreateParamsNonStreaming,
  type ChatCompletionCreateParamsBase
} from 'openai/resources/chat/completions'

/**
 * Calculate the default max tokens for a given model.
 * @see https://platform.openai.com/docs/models/overview
 * @param model Model name.
 * @returns
 */
const calcDefaultMaxTokens = (model: ChatGPTModel): number => {
  if (model.includes('16k')) return 16385
  if (model.includes('32k')) return 32768
  if (model.includes('gpt-4')) return 8192
  return 4096
}

// https://platform.openai.com/docs/api-reference/chat/create#chat-create-response_format
const allowJsonResponseFormat = (model: ChatGPTModel): boolean => {
  return model === jsonObjectModel
}

type ChatGPTModel = ChatCompletionCreateParamsBase['model']

export interface ChatGPTOptions {
  model?: ChatGPTModel
  maxTokens?: number
}

const jsonObjectModel: ChatGPTModel = 'gpt-3.5-turbo-1106'

export class ChatGPT extends LLMBase {
  client: OpenAI
  override maxTokens: number
  model: ChatGPTModel
  constructor(client: OpenAI, options?: ChatGPTOptions) {
    const model = options?.model ?? jsonObjectModel
    super()
    this.client = client
    this.model = model
    this.maxTokens = options?.maxTokens ?? calcDefaultMaxTokens(model)
  }

  async chatComplete(messages: ChatMLMessage[], config?: GenerateConfig): Promise<ChatMLResponse> {
    const args: ChatCompletionCreateParamsNonStreaming = {
      model: this.model,
      messages,
      max_tokens: config?.maxTokens,
      ...(allowJsonResponseFormat(this.model) ? { response_format: { type: 'json_object' } } : {}),
      ...config
    }
    const resp = await this.client.chat.completions.create(args)
    const choice = resp.choices[0]
    if (!choice) return { success: false, data: undefined }

    const data = choice.message.content!

    log.debug({ data }, 'openai output')

    return { success: true, data }
  }
}
