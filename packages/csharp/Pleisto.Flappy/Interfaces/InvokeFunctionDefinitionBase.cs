using Newtonsoft.Json;

namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Basic Definition of Invoke Function
  /// </summary>
  /// <typeparam name="TArgs"></typeparam>
  /// <typeparam name="TReturn"></typeparam>
  public class InvokeFunctionDefinitionBase<TArgs, TReturn>
    where TArgs : new()
    where TReturn : new()
  {
    /// <summary>
    /// Name
    /// </summary>
    [JsonRequired]
    public string Name { get; set; }

    /// <summary>
    /// Description
    /// </summary>
    public string Description { get; set; }
  }
}
