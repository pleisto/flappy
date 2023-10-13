using Microsoft.Extensions.Logging;
using OpenAI_API;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;

namespace Pleisto.Flappy.Examples.CodeInterpreter
{
  internal class CodeInterpreterCase : ExampleBase
  {
    public override async Task OnExecuteAsync()
    {
      var gpt35 = new ChatGPT(new OpenAIAPI
      {
        Auth = new APIAuthentication(apiKey: OpenAIApiKey),
        ApiUrlFormat = "https://openai.api2d.net/{0}/{1}",
        ApiVersion = "v1",
      }, "gpt-3.5-turbo", null, Logger.CreateLogger<ChatGPT>());

      var agent = new FlappyAgent(new FlappyAgentConfig
      {
        CodeInterpreter = new CodeInterpreterOptions
        {
          Env = new Dictionary<string, string>
          {
          },
          EnableNetwork = true,
        }
      }, null, null, Logger.CreateLogger<FlappyAgent>());

      await agent.CallCodeInterpreter("There are some rabbits and chickens in a barn. What is the number of chickens if there are 396 legs  and 150 heads in the barn?");
    }
  }
}
