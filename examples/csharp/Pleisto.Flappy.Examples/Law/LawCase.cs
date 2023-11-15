using McMaster.Extensions.CommandLineUtils;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;
using OpenAI_API;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Features.Syntehesized;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;
using Pleisto.Flappy.Test.Law;

namespace Pleisto.Flappy.Examples.Law
{
  [Command("law-sample")]
  internal class LawCase : ExampleBase
  {
    public override async Task OnExecuteAsync()
    {
      var gpt35 = new ChatGPT(new OpenAIAPI
      {
        Auth = new APIAuthentication(apiKey: OpenAIApiKey),
        ApiUrlFormat = OpenAIApiUrl,
        ApiVersion = "v1",
      }, "gpt-3.5-turbo", null, Logger.CreateLogger<ChatGPT>());

      var lawAgent = new FlappyAgent(new FlappyAgentConfig
      {
        LLM = gpt35,
        Features = new IFlappyFeature[]
           {
               new SynthesizedFeature<GetMeta_Args,GetMeta_Return,FlappyFeatureOption>(new SynthesizedFeatureDefinition<GetMeta_Args,GetMeta_Return>
               {
                   Name = "getMeta",
                   Description = "Extract meta data from a lawsuit full text.",
                   Args = new GetMeta_Args(),
                   ReturnType = new GetMeta_Return()
               }),
               new InvokeFeature<GetLatestLawsuits_Args,GetMeta_Args,FlappyFeatureOption>(new InvokeFeatureDefinition<GetLatestLawsuits_Args, GetMeta_Args>
               {
                   Name = "getLatestLawsuitsByPlaintiff",
                   Description= "Get the latest lawsuits by plaintiff.",
                   Args = new GetLatestLawsuits_Args(),
                   ReturnType = new GetMeta_Args(),
                   Resolve = (args) =>
                   {
                      Console.WriteLine($"====================== getLatestLawsuitsByPlaintiff call =========================");
                      Console.WriteLine($"getLatestLawsuitsByPlaintiff called");
                      Console.WriteLine(JObject.FromObject(args).ToString());
                      Console.WriteLine($"====================== getLatestLawsuitsByPlaintiff call =========================");
                      return Task.FromResult(new GetMeta_Args
                      {
                        Lawsuit =  MOCK_LAWSUIT_DATA
                      });
                   }
               })
           },
      }, null, null, Logger.CreateLogger<FlappyAgent>());
      var data = await lawAgent.ExecutePlan("Find the resume of a frontend engineer and return their metadata.");
      Console.WriteLine($"====================== Final Result =========================");
      Console.WriteLine(data.ToString());
      Console.WriteLine($"====================== Final Result Of Data =========================");
      //    Console.WriteLine(JArray.Parse(data["data"].ToString()));
    }

    private const string MOCK_LAWSUIT_DATA =
"As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong.";
  }
}
