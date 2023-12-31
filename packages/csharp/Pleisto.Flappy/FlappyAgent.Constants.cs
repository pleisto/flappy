namespace Pleisto.Flappy
{
    public partial class FlappyAgent
    {
        /// <summary>
        /// Step Prefix
        /// </summary>
        public const string STEP_PREFIX = "%@_";

        /// <summary>
        /// Resolve feature delegate
        /// </summary>
        /// <typeparam name="TArgs"></typeparam>
        /// <typeparam name="TReturn"></typeparam>
        /// <param name="args"></param>
        /// <returns></returns>
        public delegate Task<TReturn> ResolveFeature<TArgs, TReturn>(TArgs args)
          where TArgs : class
          where TReturn : class;
    }
}
