namespace Pleisto.Flappy.Interfaces
{
    /// <summary>
    /// Flappy Feature Definition
    /// </summary>
    public interface IFlappyFeatureDefinition
    {
        /// <summary>
        /// Name of feature
        /// </summary>
        public string Name { get;  set; }

        /// <summary>
        /// Description of feature
        /// </summary>
        public string Description { get; set; }
    }
}
