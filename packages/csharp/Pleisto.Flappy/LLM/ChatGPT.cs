using OpenAI_API;
using OpenAI_API.Chat;
using Pleisto.Flappy.LLM.Interfaces;

namespace Pleisto.Flappy.LLM
{
  /// <summary>
  /// LLM of ChatGPT
  /// </summary>
  public class ChatGPT : LLMBase
  {
    public int maxTokens
    {
      get; private set;
    }

    /// <summary>
    /// Calculate the default max tokens for a given model.
    /// <see cref="https://platform.openai.com/docs/models/overview"/>
    /// </summary>
    /// <param name="model"></param>
    /// <returns></returns>
    private static int calcDefaultMaxTokens(string model)
    {
      if (model.Contains("16k"))
        return 16385;
      if (model.Contains("32k"))
        return 32768;
      if (model.Contains("gpt-4"))
        return 8192;
      return 4096;
    }

    public ChatGPT(OpenAIAPI client, string model, int? maxTokens)
    {
      this.client = client;
      this.model = model;
      this.maxTokens = maxTokens ?? calcDefaultMaxTokens(model);
    }

    private readonly string model;
    private readonly OpenAIAPI client;

    /// <summary>
    /// Enable to show gpt debug info
    /// </summary>
    public bool DebugGPT { get; set; } = false;

    public virtual async Task<ChatMLResponse> chatComplete(ChatMLMessage[] message, GenerateConfig config)
    {
      if (DebugGPT)
        foreach (var i in message)
        {
          Console.WriteLine($"=========== Role:{i.role} ============");
          Console.WriteLine(i.content);
          Console.WriteLine($"===============END====================");
        }
      var resp = await client.Chat.CreateChatCompletionAsync(new ChatRequest
      {
        Model = model,
        Messages = (from i in message
                    select new ChatMessage
                    {
                      Content = i.content,
                      Role = ChatMessageRole.FromString(i.role.ToString())
                    }).ToArray(),
        //MaxTokens = (config?.maxTokens ?? maxTokens),
        Temperature = config?.temperature,
        TopP = config?.top_p,
      });

      if (DebugGPT)
        Console.WriteLine($"chatGpt:{resp.Choices[0].Message.Content}");
      if (resp?.Choices?.Any() == true)
      {
        return new ChatMLResponse
        {
          success = true,
          data = resp.Choices[0].Message.Content
        };
      }
      else
      {
        return new ChatMLResponse
        {
          success = false,
          data = null
        };
      }
    }
  }
}
