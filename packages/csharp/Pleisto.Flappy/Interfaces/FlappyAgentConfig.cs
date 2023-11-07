using Pleisto.Flappy.LLM;

namespace Pleisto.Flappy.Interfaces
{
    /// <summary>
    /// FlappyAgent Config
    /// </summary>
    public class FlappyAgentConfig
    {
        /// <summary>
        /// Which language model to use for inference.
        /// </summary>
        public ILLMBase LLM { get; set; }

        /// <summary>
        /// Which language model to use for planning.
        /// If not specified, use `llm` instead.
        /// </summary>
        public ILLMBase LLMPlaner { get; set; }

        /// <summary>
        /// Maximum number of retries when language model generation failed.
        /// </summary>
        public int? Retry { get; set; }

        /// <summary>
        /// Features
        /// </summary>
        public IFlappyFeature[] Features { get; set; }
    }
}
