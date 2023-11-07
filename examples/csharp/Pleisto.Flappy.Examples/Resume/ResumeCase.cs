using McMaster.Extensions.CommandLineUtils;
using Microsoft.Extensions.Logging;
using OpenAI_API;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Features.Syntehesized;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;
using Pleisto.Flappy.Test.Resume;

namespace Pleisto.Flappy.Examples.Resume
{
  [Command("resume-sample")]
  internal class ResumeCase : ExampleBase
  {
    private const string MOCK_LAWSUIT_DATA =
@"
I am a seasoned software engineer with over seven years of experience in front-end development. I am passionate about building excellent user interfaces, proficient in HTML, CSS, and JavaScript, and have a deep understanding of front-end frameworks such as React, Vue, and Angular. I have participated in several large-scale projects, responsible for designing and implementing front-end architecture, ensuring high performance and user-friendliness of websites. In addition, I also have project management experience, capable of leading teams to deliver high-quality outputs on time.

### Project Experience

#### 1. E-commerce Website Refactoring (ABC Company)
- Participated in the refactoring project of an e-commerce website under ABC Company, serving as the front-end technical lead.
- Rebuilt the website frontend using the React framework, implemented responsive design and dynamic loading features, improving the user experience.
- Optimized front-end performance, reducing page loading time, and improving the overall website performance.
- Designed and implemented a system for user behavior tracking and analysis, providing crucial data support for the marketing team.

#### 2. Social Media Application Development (XYZ Startup)
- Led a four-person front-end development team, developed a social media application from scratch.
- Adopted the Vue.js framework and Vuex for state management, implemented real-time chat, post publishing, and user interaction features.
- Integrated third-party login and sharing features, enhancing user registration and activity.
- Successfully launched the application to the market, with the user count growing from zero to over 50,000.

#### 3. Internal Management System Upgrade (DEF Enterprise)
- Responsible for upgrading the company's internal management system, transitioning from traditional back-end rendering to a modern front-end and back-end separate architecture.
- Developed a new front-end interface using the Angular framework, realized fast data loading and interactive features.
- Optimized data communication with the back-end using GraphQL, reducing unnecessary request times, and improving system efficiency.
- Facilitated the transition of team members to the new technology stack through training and documentation.

### Skills and Expertise

- Front-end technologies: HTML, CSS, JavaScript, React, Vue, Angular, Redux, GraphQL
- Front-end tools: Webpack, Babel, ESLint
- Project management: Agile, Scrum, Jira

### Education

- Bachelor's Degree, Computer Science, Peking University, 2012
";

    public override async Task OnExecuteAsync()
    {
      var gpt35 = new ChatGPT(new OpenAIAPI
      {
        Auth = new APIAuthentication(apiKey: OpenAIApiKey),
        ApiUrlFormat = "https://openai.api2d.net/{0}/{1}",
        ApiVersion = "v1",
      }, "gpt-3.5-turbo", null, Logger.CreateLogger<ChatGPT>());

      var lawAgent = new FlappyAgent(new FlappyAgentConfig
      {
        LLM = gpt35,
        Features = new IFlappyFeature[]
           {
               new SynthesizedFeature<GetMetaArgs,GetMetaReturn,FlappyFeatureOption>(new SynthesizedFeatureDefinition<GetMetaArgs, GetMetaReturn>
               {
                 Name = "getMeta",
                 Description = "Extract meta data from a resume full text.",
                 Args = new GetMetaArgs(),
                 ReturnType = new GetMetaReturn(),
               }),
               new InvokeFeature<GetInvokeArgs,GetMetaArgs,FlappyFeatureOption>(new InvokeFeatureDefinition<GetInvokeArgs, GetMetaArgs>
               {
                  Name = "getFrontendEngineerResumes",
                  Description = "Get all frontend engineer resumes.",
                  Args = new GetInvokeArgs(),
                  ReturnType = new GetMetaArgs(),
                  Resolve = new FlappyAgent.ResolveFeature<GetInvokeArgs, GetMetaArgs>(arg =>
                  {
                    return Task.FromResult(new GetMetaArgs
                    {
                       lawsuit = MOCK_LAWSUIT_DATA
                    });
                  })
               })
           },
      }, null, null, new LoggerFactory().CreateLogger<FlappyAgent>());
      var data = (await lawAgent.ExecutePlan("找到前端工程师的简历并返回他的元数据"));
      Console.WriteLine($"====================== Final Result =========================");
      Console.WriteLine(data.ToString());
      Console.WriteLine($"====================== Final Result Of Data =========================");
    }
  }
}
