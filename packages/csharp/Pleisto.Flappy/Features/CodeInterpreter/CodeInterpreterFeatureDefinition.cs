using Pleisto.Flappy.CodeInterpreter;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Features.CodeInterpreter
{

  /// <summary>
  /// Code Interpreter feature
  /// </summary>
  /// <typeparam name="TArgs"></typeparam>
  /// <typeparam name="TReturn"></typeparam>
  internal class CodeInterpreterFeatureDefinition<TArgs, TReturn> : InvokeFeatureDefinition<TArgs, TReturn>, IFlappyFeatureDefinition<TArgs, TReturn>
    where TArgs : CodeInterpreterInput
    where TReturn : CodeInterpreterOutput
  {
  }
}
