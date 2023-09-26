using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy
{
  /// <summary>
  /// Invoke Function
  /// </summary>
  /// <typeparam name="TArgs"></typeparam>
  /// <typeparam name="TReturn"></typeparam>
  public class InvokeFunction<TArgs, TReturn> : FlappyFunctionBase<TArgs, TReturn>, IFlappyFunction
      where TArgs : class
      where TReturn : class
  {
    /// <summary>
    /// Create a invoke function
    /// </summary>
    /// <param name="define">Invoke function define</param>
    public InvokeFunction(InvokeFunctionDefinition<TArgs, TReturn> define) : base(define)
    {
    }

    /// <summary>
    /// Invoke function call
    /// </summary>
    /// <param name="agent"></param>
    /// <param name="args"></param>
    /// <returns></returns>
    public override async Task<TReturn> Call(FlappyAgent agent, TArgs args)
    {
      return await Define.Resolve(args);
    }
  }
}
