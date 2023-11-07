namespace Pleisto.Flappy.Interfaces
{
  /// <summary>
  /// Flappy Feature Definition
  /// </summary>
  /// <typeparam name="TArgs">Argument of Feature Call</typeparam>
  /// <typeparam name="TReturn">Function Return</typeparam>
  public interface IFlappyFeatureDefinition<TArgs, TReturn>
    where TArgs : class
    where TReturn : class
  {
  }
}
