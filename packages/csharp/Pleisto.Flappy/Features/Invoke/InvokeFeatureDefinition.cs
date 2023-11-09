using Newtonsoft.Json;
using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Features.Invoke
{
    /// <summary>
    /// Definition of Invoke Feature
    /// </summary>
    /// <typeparam name="TArgs">Argument of feature</typeparam>
    /// <typeparam name="TReturn">Return of feature</typeparam>
    public class InvokeFeatureDefinition<TArgs, TReturn> : FeaturesDefinitionBase<TArgs, TReturn>, IFlappyFeatureDefinition
      where TArgs : class
      where TReturn : class
    {
        /// <summary>
        /// Resolve Feature
        /// </summary>
        [JsonIgnore]
        public FlappyAgent.ResolveFeature<TArgs, TReturn> Resolve { get; set; }
    }
}
