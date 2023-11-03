import { LLMBase } from './llm-base'
import { type ChatMLMessage, type ChatMLResponse, type GenerateConfig } from './interface'
import { randomUUID, createHash } from 'crypto'
import { log } from '../utils'
import { type JsonObject } from 'roarr/dist/types'

// https://platform.baichuan-ai.com/docs/api
type BaichuanModel = string
const BAICHUAN_MODEL: BaichuanModel = 'Baichuan2-53B'

export interface BaichuanArgs {
  baichuan_api_key: string
  baichuan_secret_key: string
  model: BaichuanModel
}

interface BaichuanParameters {
  // [0, 1], default 0.3
  temperature?: number
  // [0, 20], default 5
  top_k?: number
  // [0, 1], default 0.85
  top_p?: number
  // default false
  with_search_enhance?: boolean
}

interface BaichuanRequestBody {
  model: BaichuanModel
  messages: ChatMLMessage[]
  parameters?: BaichuanParameters
}

interface BaichuanChatMLMessage {
  role: 'assistant'
  content: string
  finish_reason: string
}

interface BaichuanResponseUsage {
  prompt_tokens: number
  answer_tokens: number
  total_tokens: number
}

interface BaichuanResponseBody {
  code: number
  msg: string
  data: {
    messages?: BaichuanChatMLMessage[]
  }
  usage: BaichuanResponseUsage
}

const calculateMd5 = (input: string): string => createHash('md5').update(input).digest('hex')

export class Baichuan extends LLMBase {
  private readonly model: BaichuanModel
  private readonly baichuan_api_key: string
  private readonly baichuan_secret_key: string
  override maxTokens = -1

  constructor(args?: BaichuanArgs) {
    super()
    this.model = args?.baichuan_api_key ?? BAICHUAN_MODEL
    this.baichuan_api_key = args?.baichuan_api_key ?? process.env.BAICHUAN_API_KEY!
    if (!this.baichuan_api_key) throw new Error('BAICHUAN_API_KEY not found')
    this.baichuan_secret_key = args?.baichuan_secret_key ?? process.env.BAICHUAN_SECRET_KEY!
    if (!this.baichuan_secret_key) throw new Error('BAICHUAN_SECRET_KEY not found')
  }

  async chatComplete(messages: ChatMLMessage[], config?: GenerateConfig): Promise<ChatMLResponse> {
    const requestId = randomUUID()
    const body: BaichuanRequestBody = {
      model: this.model,
      messages,
      parameters: {
        ...(config?.temperature ? { temperature: config.temperature } : {}),
        ...(config?.top_p ? { top_p: config.top_p } : {})
      }
    }

    const jsonBody = JSON.stringify(body)
    const timestamp = Math.round(new Date().getTime() / 1000).toString()
    const signature = calculateMd5(this.baichuan_secret_key + jsonBody + timestamp)
    const headers = {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.baichuan_api_key}`,
      'X-BC-Request-Id': requestId,
      'X-BC-Timestamp': timestamp,
      'X-BC-Signature': signature,
      'X-BC-Sign-Algo': 'MD5'
    }

    try {
      log.debug({ data: { jsonBody, headers } }, 'Request baichuan API')
      const response = await fetch('https://api.baichuan-ai.com/v1/chat', {
        method: 'POST',
        body: jsonBody,
        headers
      })

      if (response.status !== 200) {
        return { success: false, data: `Invalid status: ${response.status}` }
      }

      const result: BaichuanResponseBody = (await response.json()) as BaichuanResponseBody
      log.debug({ data: result } as unknown as JsonObject, 'baichuan API response')

      if (result.code !== 0) return { success: false, data: result.msg }

      const message = result.data?.messages?.[0]
      if (!message) return { success: false, data: 'Invalid response' }

      return { success: true, data: message.content }
    } catch (error) {
      return { success: false, data: undefined }
    }
  }
}
