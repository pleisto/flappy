using Newtonsoft.Json;
using static Pleisto.Flappy.FlappyAgent;

namespace Pleisto.Flappy.Interfaces
{
  public class InvokeFunctionDefinition<TArgs, TReturn> : FunctionsDefinitionBase<TArgs, TReturn>
    where TArgs : class
    where TReturn : class
  {
    [JsonIgnore]
    public ResolveFunction<TArgs, TReturn> resolve { get; set; }
  }
}
