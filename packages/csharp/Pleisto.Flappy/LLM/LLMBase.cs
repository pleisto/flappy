using Pleisto.Flappy.LLM.Interfaces;

namespace Pleisto.Flappy.LLM
{
    /// <summary>
    /// LLM Base
    /// </summary>
    public interface ILLMBase
    {
        /// <summary>
        /// Max Tokens of LLM
        /// </summary>
        int MaxTokens { get; }

        /// <summary>
        /// Run LLM Complete
        /// </summary>
        /// <param name="message">Request message</param>
        /// <param name="config">Generation Configure</param>
        /// <returns></returns>
        Task<ChatMLResponse> ChatComplete(ChatMLMessage[] message, GenerateConfig config = null);
    }
}
