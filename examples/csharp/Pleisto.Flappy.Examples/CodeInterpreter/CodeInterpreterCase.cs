using McMaster.Extensions.CommandLineUtils;
using Microsoft.Extensions.Logging;
using OpenAI_API;
using Pleisto.Flappy.Features.CodeInterpreter;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;

namespace Pleisto.Flappy.Examples.CodeInterpreter
{
  [Command("code-interpreter-sample")]
  internal class CodeInterpreterCase : ExampleBase
  {
    public override async Task OnExecuteAsync()
    {
      var gpt35 = new ChatGPT(new OpenAIAPI
      {
        Auth = new APIAuthentication(apiKey: OpenAIApiKey),
        ApiUrlFormat =OpenAIApiUrl,
        ApiVersion = "v1",
      }, "gpt-3.5-turbo", null, Logger.CreateLogger<ChatGPT>());

      var agent = new FlappyAgent(new FlappyAgentConfig
      {
        LLM = gpt35,
        Features = new IFlappyFeature[]
         {
           new CodeInterpreterFeature(null,new CodeInterpreterOptions
           {
              EnableNetwork=false,
           })
         }
      }, null, null, Logger.CreateLogger<FlappyAgent>());
      var data = await agent.ExecutePlan("There are some rabbits and chickens in a barn. What is the number of chickens if there are 396 legs and 150 heads in the barn?");
      Console.WriteLine($"====================== Final Result =========================");
      Console.WriteLine(data.ToString());
      Console.WriteLine($"====================== Final Result Of Data =========================");
    }
  }
}
