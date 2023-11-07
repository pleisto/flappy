using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Features.Syntehesized
{
  /// <summary>
  /// Definition of Synthesized feature
  /// </summary>
  /// <typeparam name="TArgs"></typeparam>
  /// <typeparam name="TReturn"></typeparam>
  public class SynthesizedFeatureDefinition<TArgs, TReturn> : InvokeFeatureDefinition<TArgs, TReturn>, IFlappyFeatureDefinition<TArgs, TReturn>
    where TArgs : class
    where TReturn : class
  {
  }
}
