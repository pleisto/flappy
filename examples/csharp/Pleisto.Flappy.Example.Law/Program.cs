using Newtonsoft.Json.Linq;
using OpenAI_API;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;

namespace Pleisto.Flappy.Test.Law
{
  public static class Program
  {
    private static string OpenApiKey => Environment.GetEnvironmentVariable("OPENAI_API_KEY") ?? throw new Exception("no environment found: OPENAI_API_KEY");

    public static bool ConsoleRun { get; set; } = true;

    public static async Task Main()
    {
      try
      {
        var gpt35 = new ChatGPT(new OpenAIAPI
        {
          Auth = new APIAuthentication(apiKey: OpenApiKey),
          ApiUrlFormat = "https://openai.api2d.net/{0}/{1}",
          ApiVersion = "v1",
        }, "gpt-3.5-turbo", null);

        var lawAgent = new FlappyAgent(new FlappyAgentConfig
        {
          llm = gpt35,
          functions = new FlappyFunction[]
             {
                         new SynthesizedFunction<getMeta_Args,getMeta_Return>(new SynthesizedFunctionDefinition<getMeta_Args,getMeta_Return>
                         {
                             name = "getMeta",
                             description = "Extract meta data from a lawsuit full text.",
                             args = new getMeta_Args(),
                             returnType = new getMeta_Return(),
                         }),
                         new InvokeFunctions<getLatestLawsuits_Args,getMeta_Args>(new InvokeFunctionDefinition<getLatestLawsuits_Args, getMeta_Args>
                         {
                             name = "getLatestLawsuitsByPlaintiff",
                             description= "Get the latest lawsuits by plaintiff.",
                             args = new getLatestLawsuits_Args(),
                             returnType = new getMeta_Args(),
                             resolve = (args) =>
                             {
                                 Console.WriteLine($"====================== getLatestLawsuitsByPlaintiff call =========================");
                                 Console.WriteLine($"getLatestLawsuitsByPlaintiff called");
                                 Console.WriteLine(JObject.FromObject(args).ToString());
                                 Console.WriteLine($"====================== getLatestLawsuitsByPlaintiff call =========================");
                                return Task.FromResult(new getMeta_Args
                                {
                                     lawsuit =MOCK_LAWSUIT_DATA
                                });
                             }
                         })
             },
        }, null, null);
        var data = (await lawAgent.createExecutePlan("找到原告为张三的最新案件并返回它的元数据"));
        Console.WriteLine($"====================== Final Result =========================");
        Console.WriteLine(data.ToString());
        Console.WriteLine($"====================== Final Result Of Data =========================");
        //    Console.WriteLine(JArray.Parse(data["data"].ToString()));
      }
      catch (Exception ex) when (ConsoleRun)
      {
        Console.Error.WriteLine(ex.ToString());
      }
    }

    private const string MOCK_LAWSUIT_DATA =
"As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong.";
  }
}
