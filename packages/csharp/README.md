# Flappy CSharp Version

[![License](https://img.shields.io/github/license/pleisto/flappy)](../../LICENSE)
[![NuGet version (Pleisto.Flappy)](https://img.shields.io/nuget/v/Pleisto.Flappy.svg?style=flat-square)](https://www.nuget.org/packages/Pleisto.Flappy/)
[![NUnit test](https://github.com/pleisto/flappy/actions/workflows/csharp-test.yml/badge.svg)](https://github.com/pleisto/flappy/actions/workflows/csharp-test.yml)

This package is the CSharp version of the flappy implementation.

## Usage

### Installation

```
Install-Package Pleisto.Flappy
```


### Create a Synthesized Function
 * for csharp, the function return type and function argument type must be convert to json
```csharp
    class getMeta_Args
    {
        public string lawsuit { get; set; }

        public override string ToString()
        {
            return JObject.FromObject(this).ToString();
        }
    }

    class getMeta_Return
    {
        [JsonRequired]
        [JsonConverter(typeof(StringEnumConverter))]
        [DefaultValue(Verdict.Unknow)]
        public Verdict verdict { get; set; } = Verdict.Unknow;

        [JsonRequired]
        public string plaintiff { get; set; } = string.Empty;

        [JsonRequired]
        public string defendant { get; set; } = string.Empty;

        [JsonRequired]
        public string[] judgeOptions { get; set; } = Array.Empty<string>();

        public override string ToString()
        {
            return JObject.FromObject(this).ToString();
        }
    }

    enum Verdict
    {
        Innocent,
        Guilty,
        Unknow
    }

    var synthesizedFunction = new SynthesizedFunction<getMeta_Args,getMeta_Return>(new SynthesizedFunctionDefinition<getMeta_Args,getMeta_Return>
    {
        name = "getMeta",
        description = "Extract meta data from a lawsuit full text.",
        args = new getMeta_Args(),
        returnType = new getMeta_Return(),
    }),
```

### Create an Invoke Function
In addition to synthesized functions, developers can also add custom methods for the agent to invoke by including invokeFunction.

``` csharp

    private const string MOCK_LAWSUIT_DATA =
          "As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong.";
    var invokeFunction = new InvokeFunctions<getLatestLawsuits_Args,getMeta_Args>(new InvokeFunctionDefinition<getLatestLawsuits_Args, getMeta_Args>
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
```



### Create an agent
To create an agent, you need to provide an LLM (Large Language Model) along with the methods you want the agent to use.

```csharp
var gpt35 = new ChatGPT(new OpenAIAPI
{
    Auth = new APIAuthentication(apiKey: OpenApiKey),
    ApiUrlFormat = "https://openai.api2d.net/{0}/{1}",
    ApiVersion = "v1",
}, "gpt-3.5-turbo", null);

var lawAgent = new FlappyAgent(new FlappyAgentConfig
{
    llm = gpt35,
    functions = new FlappyFunction[] {
        synthesizedFunction,
        invokeFunction
    }
},null,null);
```


## Todo
