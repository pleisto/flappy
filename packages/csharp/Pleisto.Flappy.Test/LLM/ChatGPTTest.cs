using NUnit.Framework;
using OpenAI_API;
using Pleisto.Flappy.LLM;
using System.Diagnostics.CodeAnalysis;

namespace Pleisto.Flappy.Tests.LLM
{
    /// <summary>
    /// ChatGPT test
    /// </summary>
    public class ChatGPTTest : LLMTest<ChatGPT>
    {
        [SuppressMessage("Performance", "CA1822")]
        private string OpenAIApiKey => Environment.GetEnvironmentVariable("OPENAI_API_KEY");

        protected override ChatGPT OnLLMCreate()
        {
            if (OpenAIApiKey == null)
                Assert.Pass("SKIP!");
            return new ChatGPT(new OpenAIAPI
            {
                Auth = new APIAuthentication(apiKey: OpenAIApiKey),
                ApiUrlFormat = Environment.GetEnvironmentVariable("OPENAI_API_URL"),
                ApiVersion = "v1",
            }, "gpt-3.5-turbo", null, null);
        }
    }
}
