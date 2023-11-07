using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;
using OpenAI_API;
using Pleisto.Flappy.CodeInterpreter;
using Pleisto.Flappy.Features.CodeInterpreter;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;
using System.Diagnostics;

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

  internal class NativeCall
  {
    private const string pythonInspectCode = @"
print('Hello World');
";

    public void OnExecute()
    {
      if (NativeHandler.NativeCall())
      {
        Console.WriteLine("Native call success!");
        Stopwatch timer = new Stopwatch();
        timer.Start();
        var result = NativeHandler.EvalPythonCode(pythonInspectCode, false, new Dictionary<string, string>
        {
        });
        timer.Stop();
        Console.WriteLine($"Result of Execute:");
        Console.WriteLine(JObject.FromObject(result).ToString());
        Console.WriteLine("Elasped ms:" + timer.ElapsedMilliseconds);
      }
      else
      {
        Console.WriteLine("Native call faild!");
        return;
      }
    }
  }
}
