using OpenAI_API;
using OpenAI_API.Chat;
using Pleisto.Flappy.LLM.Interfaces;

namespace Pleisto.Flappy.LLM
{
  /// <summary>
  /// LLM of ChatGPT
  /// </summary>
  public class ChatGPT : ILLMBase
  {
    /// <summary>
    /// Max Tokens
    /// </summary>
    public int MaxTokens
    {
      get; private set;
    }

    /// <summary>
    /// Calculate the default max tokens for a given model.
    /// <see cref="https://platform.openai.com/docs/models/overview"/>
    /// </summary>
    /// <param name="model"></param>
    /// <returns></returns>
    private static int CalcDefaultMaxTokens(string model)
    {
      if (model.Contains("16k"))
        return 16385;
      if (model.Contains("32k"))
        return 32768;
      if (model.Contains("gpt-4"))
        return 8192;
      return 4096;
    }

    /// <summary>
    /// ChatGPT LLM
    /// </summary>
    /// <param name="client">OpenAI Client</param>
    /// <param name="model">Used model</param>
    /// <param name="maxTokens">
    /// Max Tokens null by AutoDetect
    /// <see cref="https://platform.openai.com/docs/models/overview"/>
    /// </param>
    public ChatGPT(OpenAIAPI client, string model, int? maxTokens)
    {
      this.client = client;
      this.model = model;
      this.MaxTokens = maxTokens ?? CalcDefaultMaxTokens(model);
    }

    private readonly string model;
    private readonly OpenAIAPI client;

    /// <summary>
    /// Enable to show gpt debug info
    /// </summary>
    public bool DebugGPT { get; set; } = false;

    /// <summary>
    /// Run ChatGPT Complete
    /// </summary>
    /// <param name="message">Request Message</param>
    /// <param name="config">Config</param>
    /// <returns></returns>
    public virtual async Task<ChatMLResponse> ChatComplete(ChatMLMessage[] message, GenerateConfig config)
    {
      if (DebugGPT)
        foreach (var i in message)
        {
          Console.WriteLine($"=========== Role:{i.Role} ============");
          Console.WriteLine(i.Content);
          Console.WriteLine($"===============END====================");
        }
      var resp = await client.Chat.CreateChatCompletionAsync(new ChatRequest
      {
        Model = model,
        Messages = (from i in message
                    select new ChatMessage
                    {
                      Content = i.Content,
                      Role = ChatMessageRole.FromString(i.Role.ToString())
                    }).ToArray(),
        //MaxTokens = (config?.maxTokens ?? maxTokens),
        Temperature = config?.Temperature,
        TopP = config?.Top_P,
      });

      if (DebugGPT)
        Console.WriteLine($"chatGpt:{resp.Choices[0].Message.Content}");
      if (resp?.Choices?.Any() == true)
      {
        return new ChatMLResponse
        {
          Success = true,
          Data = resp.Choices[0].Message.Content
        };
      }
      else
      {
        return new ChatMLResponse
        {
          Success = false,
          Data = null
        };
      }
    }
  }
}
