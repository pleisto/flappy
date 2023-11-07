using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Features.CodeInterpreter
{
  /// <summary>
  /// Code Interpreter feature
  /// </summary>
  /// <typeparam name="TArgs"></typeparam>
  /// <typeparam name="TReturn"></typeparam>
  public class CodeInterpreterFeatureDefinition<TArgs, TReturn> : IFlappyFeatureDefinition<TArgs, TReturn>
    where TArgs : CodeInterpreterInput
    where TReturn : CodeInterpreterOutput
  {
    /// <summary>
    /// Name of CodeInterpreter
    /// </summary>
    public string Name { get; internal set; }

    /// <summary>
    /// Description of CodeInterpreter
    /// </summary>
    public string Description { get; internal set; }
  }
}
