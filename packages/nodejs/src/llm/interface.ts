export interface ChatMLMessage {
  role: 'system' | 'user' | 'assistant'
  content: string
}

export interface GenerateConfig {
  maxTokens: number
  temperature: number
  top_p: number
}

export interface ChatMLResponse {
  success: boolean
  data: string | undefined
}
