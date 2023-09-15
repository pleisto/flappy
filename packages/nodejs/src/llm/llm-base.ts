import { type ChatMLMessage, type ChatMLResponse, type GenerateConfig } from './interface'

export abstract class LLMBase {
  abstract maxTokens: number

  public abstract chatComplete(messages: ChatMLMessage[], config?: GenerateConfig): Promise<ChatMLResponse>
}
