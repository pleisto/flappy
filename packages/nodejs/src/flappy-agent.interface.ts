import { type AnyFlappyFeature } from './flappy-feature'
import { type LLMBase } from './llms/llm-base'

export interface FlappyAgentInterface {
  llm: LLMBase
  retry: number
}

/**
 * FlappyAgent Config
 */
export interface FlappyAgentConfig<TFeatures extends readonly AnyFlappyFeature[] = readonly AnyFlappyFeature[]> {
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
   * List of features that can be called by the agent or language model.
   */
  features: TFeatures

  /**
   * Maximum number of retries when language model generation failed.
   * The default retries is 1.
   */
  retry?: number
}
