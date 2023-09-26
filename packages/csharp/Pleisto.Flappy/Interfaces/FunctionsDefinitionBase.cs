using Newtonsoft.Json;

namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Base Definition of Function
  /// </summary>
  /// <typeparam name="TArgs">Function call Args</typeparam>
  /// <typeparam name="TReturns">Function Return</typeparam>
  public class FunctionsDefinitionBase<TArgs, TReturns>
    where TArgs : class
    where TReturns : class
  {
    /// <summary>
    /// Function Name
    /// </summary>
    public string Name { get; set; }

    /// <summary>
    /// Function Description
    /// </summary>
    public string Description { get; set; } = null;

    /// <summary>
    /// Function Argument
    /// </summary>
    [JsonRequired]
    public TArgs Args { get; set; }

    /// <summary>
    /// Function ReturnType
    /// </summary>
    [JsonRequired]
    public TReturns ReturnType { get; set; }
  }
}
