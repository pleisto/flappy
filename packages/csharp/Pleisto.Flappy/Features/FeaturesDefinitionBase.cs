using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace Pleisto.Flappy.Features
{
    /// <summary>
    /// Base Definition of Features
    /// </summary>
    /// <typeparam name="TArgs">Features call Args</typeparam>
    /// <typeparam name="TReturns">Features Return</typeparam>
    public class FeaturesDefinitionBase<TArgs, TReturns>
      where TArgs : class
      where TReturns : class
    {
        /// <summary>
        /// Feature Name
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// Feature Description
        /// </summary>
        public string Description { get; set; } = null;

        /// <summary>
        /// Feature Argument
        /// </summary>
        [JsonRequired]
        public TArgs Args { get; set; }

        /// <summary>
        /// Feature ReturnType
        /// </summary>
        [JsonRequired]
        public TReturns ReturnType { get; set; }
    }
}
