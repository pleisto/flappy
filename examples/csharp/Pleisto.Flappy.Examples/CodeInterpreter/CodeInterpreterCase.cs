using Microsoft.Extensions.Logging;
using OpenAI_API;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.LLM;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Pleisto.Flappy.Examples.CodeInterpreter
{
  internal class CodeInterpreterCase : ExampleBase
  {
    public override Task OnExecuteAsync()
    {
      return Task.CompletedTask;
    }
  }
}
