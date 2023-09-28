using OpenAI_API;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;

namespace Pleisto.Flappy.Test.Resume
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
        }, "gpt-3.5-turbo", null)
        {
          DebugGPT = true
        };

        var lawAgent = new FlappyAgent(new FlappyAgentConfig
        {
          LLM = gpt35,
          Functions = new IFlappyFunction[]
             {
               new SynthesizedFunction<GetMetaArgs,GetMetaReturn>(new SynthesizedFunctionDefinition<GetMetaArgs, GetMetaReturn>
               {
                 Name = "getMeta",
                 Description = "Extract meta data from a resume full text.",
                 Args = new GetMetaArgs(),
                 ReturnType = new GetMetaReturn(),
               }),
               new InvokeFunction<GetInvokeArgs,GetMetaArgs>(new InvokeFunctionDefinition<GetInvokeArgs, GetMetaArgs>
               {
                  Name = "getFrontendEngineerResumes",
                  Description = "Get all frontend engineer resumes.",
                  Args = new GetInvokeArgs(),
                  ReturnType = new GetMetaArgs(),
                  Resolve = new FlappyAgent.ResolveFunction<GetInvokeArgs, GetMetaArgs>(arg =>
                  {
                    return Task.FromResult(new GetMetaArgs
                    {
                       lawsuit = MOCK_LAWSUIT_DATA
                    });
                  })
               })
             },
        }, null, null);
        var data = (await lawAgent.CreateExecutePlan("找到前端工程师的简历并返回他的元数据"));
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
@"
我是一名资深的软件工程师，拥有超过七年的前端开发经验。我热衷于构建出色的用户界面，熟练运用HTML、CSS和JavaScript，并精通React、Vue以及Angular等前端框架。我曾参与过多个大型项目，负责设计和实现前端架构，确保网站的高性能和用户友好性。此外，我还具备项目管理的经验，能够带领团队按时交付高质量的成果。

### 项目经历

#### 1. 电商网站重构 (ABC 公司)
- 负责参与了ABC公司旗下电商网站的重构项目，担任前端技术负责人。
- 使用React框架重建网站前端，实现了页面响应式设计和动态加载功能，提升了用户体验。
- 优化前端性能，减少了页面加载时间，提高了网站整体性能。
- 设计并实施了用户行为跟踪和分析系统，为市场营销团队提供了关键的数据支持。

#### 2. 社交媒体应用开发 (XYZ 创业公司)
- 领导一个四人的前端开发团队，从零开始开发了一款社交媒体应用。
- 采用了Vue.js框架和Vuex进行状态管理，实现了实时聊天、帖子发布和用户互动功能。
- 集成了第三方登录和分享功能，提升了用户注册和活跃度。
- 成功将应用推向市场，用户数量从零增长到五万以上。

#### 3. 内部管理系统升级 (DEF 企业)
- 负责升级公司内部管理系统，从传统的后端渲染转变为现代化的前后端分离架构。
- 使用Angular框架开发新的前端界面，实现了快速的数据加载和交互功能。
- 利用GraphQL优化了与后端的数据通信，减少了不必要的请求次数，提高了系统效率。
- 通过培训和文档编写，帮助团队成员顺利过渡到新的技术栈。

### 技能和专业知识

- 前端技术: HTML, CSS, JavaScript, React, Vue, Angular, Redux, GraphQL
- 前端工具: Webpack, Babel, ESLint
- 项目管理: Agile, Scrum, Jira

### 教育背景

- 学士学位，计算机科学，北京大学，2012年
";
  }
}
