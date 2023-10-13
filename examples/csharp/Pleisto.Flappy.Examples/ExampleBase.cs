using McMaster.Extensions.CommandLineUtils;
using Microsoft.Extensions.Logging;

namespace Pleisto.Flappy.Examples
{
  internal abstract class ExampleBase
  {
    public abstract Task OnExecuteAsync();

    [Option("--openai-api-key", Description = "OpenAI API Key, also from environment: OPENAI_API_KEY")]
    public string OpenAIApiKey { get; set; } = Environment.GetEnvironmentVariable("OPENAI_API_KEY");

    [Option("--openai-api-url")]
    public string OpenAIApiUrl { get; set; } = "https://openai.api2d.net/{0}/{1}";

    protected static readonly ILoggerFactory Logger = LoggerFactory.Create(builder =>
     {
       builder.AddConsole();
     });
  }
}
