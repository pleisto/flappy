namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Flappy Function Definition
  /// </summary>
  /// <typeparam name="TArgs">Argument of Function Call</typeparam>
  /// <typeparam name="TReturn">Function Return</typeparam>
  public interface IFlappyFunctionDefinition<TArgs, TReturn>
    where TArgs : new()
    where TReturn : new()
  {
  }
}
