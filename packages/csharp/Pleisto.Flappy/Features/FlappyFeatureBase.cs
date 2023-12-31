using Newtonsoft.Json.Linq;
using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Features
{
    /// <summary>
    /// Basic of flappy fature
    /// </summary>
    /// <typeparam name="TArgs"></typeparam>
    /// <typeparam name="TReturn"></typeparam>
    /// <typeparam name="TOptions"></typeparam>
    public abstract class FlappyFeatureBase<TArgs, TReturn, TOptions> : IFlappyFeature
        where TArgs : class
        where TReturn : class
        where TOptions : FlappyFeatureOption
    {
        /// <summary>
        /// Feature definition
        /// </summary>
        public IFlappyFeatureDefinition Define { get; private set; }

        /// <summary>
        /// Feature options
        /// </summary>
        public TOptions Options { get; set; }

        /// <summary>
        /// Fature calling schema
        /// </summary>
        public JObject CallingSchema { get; private set; }

        /// <summary>
        /// Name of fature
        /// </summary>
        public string Name => Define.Name;

        /// <summary>
        /// Create Flappy Fature Base
        /// </summary>
        /// <param name="define">Feature Definition</param>
        public FlappyFeatureBase(IFlappyFeatureDefinition define)
        {
            Define = define;
            CallingSchema = BuildJsonSchema(define);
        }

        internal static JObject BuildJsonSchema(IFlappyFeatureDefinition define)
        {
            return new JObject
            {
                ["name"] = define.Name,
                ["description"] = define.Description,
                ["parameters"] = new JObject
                {
                    ["type"] = "object",
                    ["properties"] = new JObject
                    {
                        ["args"] = JObject.FromObject(JsonSchema.FromType<TArgs>()),
                        ["returnType"] = JObject.FromObject(JsonSchema.FromType<TReturn>())
                    }
                }
            };
        }

        /// <summary>
        /// Fature call abstract
        /// </summary>
        /// <param name="agent">FlappyAgent caller</param>
        /// <param name="args">Calling argument</param>
        /// <returns></returns>
        public abstract Task<TReturn> Call(FlappyAgent agent, TArgs args);

        async Task<object> IFlappyFeature.SystemCall(FlappyAgent agent, object args)
        {
            TArgs argsType = args as TArgs;

            if (argsType == null)
                throw new InvalidCastException($"unable to convert {args.GetType().FullName} to {typeof(TArgs).FullName}");
            return await Call(agent, argsType);
        }

        object IFlappyFeature.JsonToArgs(JObject json)
        {
            return json.ToObject<TArgs>();
        }
    }
}
