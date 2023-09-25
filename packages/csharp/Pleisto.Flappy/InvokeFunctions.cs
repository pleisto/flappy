using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy
{
  public class InvokeFunctions<TArgs, TReturn> : FlappyFunctionBase<TArgs, TReturn>, FlappyFunction
      where TArgs : class
      where TReturn : class
  {
    public InvokeFunctions(InvokeFunctionDefinition<TArgs, TReturn> define) : base(define)
    {
    }

    public override async Task<TReturn> call(FlappyAgent agent, TArgs args)
    {
      return await define.resolve(args);
    }
  }
}
