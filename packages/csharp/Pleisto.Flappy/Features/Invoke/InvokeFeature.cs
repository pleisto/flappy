using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Features.Invoke
{
    /// <summary>
    /// Invoke Function
    /// </summary>
    /// <typeparam name="TArgs">Argument of feature</typeparam>
    /// <typeparam name="TReturn">Return of feature</typeparam>
    /// <typeparam name="TOptions">Options of feature</typeparam>
    public class InvokeFeature<TArgs, TReturn, TOptions> : FlappyFeatureBase<TArgs, TReturn, TOptions>, IFlappyFeature
      where TArgs : class
      where TReturn : class
      where TOptions : FlappyFeatureOption
    {
        /// <summary>
        /// Create a invoke feature
        /// </summary>
        /// <param name="define">Invoke feature define</param>
        public InvokeFeature(InvokeFeatureDefinition<TArgs, TReturn> define) : base(define)
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
            return await (Define as InvokeFeatureDefinition<TArgs, TReturn>).Resolve(args);
        }
    }
}
