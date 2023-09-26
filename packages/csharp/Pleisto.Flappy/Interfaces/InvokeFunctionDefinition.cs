using Newtonsoft.Json;
using static Pleisto.Flappy.FlappyAgent;

namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Definition of Invoke Function
  /// </summary>
  /// <typeparam name="TArgs"></typeparam>
  /// <typeparam name="TReturn"></typeparam>
  public class InvokeFunctionDefinition<TArgs, TReturn> : FunctionsDefinitionBase<TArgs, TReturn>
    where TArgs : class
    where TReturn : class
  {
    /// <summary>
    /// Resolve Function
    /// </summary>
    [JsonIgnore]
    public ResolveFunction<TArgs, TReturn> Resolve { get; set; }
  }
}
