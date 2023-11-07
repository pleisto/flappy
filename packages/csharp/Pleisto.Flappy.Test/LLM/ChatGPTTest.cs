using NUnit.Framework;
using OpenAI_API;
using Pleisto.Flappy.LLM;

namespace Pleisto.Flappy.Tests.LLM
{
  public class ChatGPTTest : LLMTest<ChatGPT>
  {
    private string OpenAIApiKey => Environment.GetEnvironmentVariable("OPENAI_API_KEY");

    protected override ChatGPT OnLLMCreate()
    {
      if (OpenAIApiKey == null)
        Assert.Pass("SKIP!");
      return new ChatGPT(new OpenAIAPI
      {
        Auth = new APIAuthentication(apiKey: OpenAIApiKey),
        ApiUrlFormat = "https://openai.api2d.net/{0}/{1}",
        ApiVersion = "v1",
      }, "gpt-3.5-turbo", null, null);
    }
  }
}
