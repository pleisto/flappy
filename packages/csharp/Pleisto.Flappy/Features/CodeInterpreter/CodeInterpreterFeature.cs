using Pleisto.Flappy.CodeInterpreter;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Features.CodeInterpreter
{
  /// <summary>
  /// CodeInterpreter feature
  /// </summary>
  /// <typeparam name="TArgs">Argument of feature</typeparam>
  /// <typeparam name="TReturn">Return of feature</typeparam>
  /// <typeparam name="TOptions">Options of feature</typeparam>
  public class CodeInterpreterFeature<TArgs, TReturn, TOptions> : FlappyFeatureBase<TArgs, TReturn, TOptions>, IFlappyFeature
    where TArgs : CodeInterpreterInput
    where TReturn : CodeInterpreterOutput, new()
    where TOptions : CodeInterpreterOptions
  {
    /// <summary>
    /// Create CodeInterpreter feature
    /// </summary>
    /// <param name="define"></param>
    public CodeInterpreterFeature(InvokeFeatureDefinition<TArgs, TReturn> define) : base(define)
    {
    }

    /// <summary>
    /// CodeInterpreter call entry
    /// </summary>
    /// <param name="agent"></param>
    /// <param name="args"></param>
    /// <returns></returns>
    /// <exception cref="NotImplementedException"></exception>
    public override Task<TReturn> Call(FlappyAgent agent, TArgs args)
    {
      var result = NativeHandler.EvalPythonCode(args.Code, Options?.EnableNetwork ?? false, Options?.Env ?? new Dictionary<string, string>(), Options?.CacheDir);
      if (string.IsNullOrWhiteSpace(result.StdErr) == false)
        throw new Exception(result.StdErr);
      return Task.FromResult(new TReturn
      {
        Result = result.StdOut
      });
    }
  }
}
