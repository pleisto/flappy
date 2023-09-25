using Pleisto.Flappy.LLM.Interfaces;

namespace Pleisto.Flappy.LLM
{
  public interface LLMBase
  {
    int maxTokens { get; }

    Task<ChatMLResponse> chatComplete(ChatMLMessage[] message, GenerateConfig config = null);
  }
}
