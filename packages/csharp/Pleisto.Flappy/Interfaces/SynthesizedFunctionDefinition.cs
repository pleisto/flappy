namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Definition of Synthesized function
  /// </summary>
  /// <typeparam name="TArgs"></typeparam>
  /// <typeparam name="TReturn"></typeparam>
  public class SynthesizedFunctionDefinition<TArgs, TReturn> : InvokeFunctionDefinition<TArgs, TReturn>, IFlappyFunctionDefinition<TArgs, TReturn>  
    where TArgs : class
    where TReturn : class
  {
  }
}
