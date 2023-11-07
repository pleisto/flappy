namespace Pleisto.Flappy.LLM.Interfaces
{
    /// <summary>
    /// LLM Generation Config
    /// </summary>
    public class GenerateConfig
    {
        /// <summary>
        /// MaxTokens
        /// </summary>
        public int MaxTokens { get; set; }

        /// <summary>
        /// Temperature
        /// </summary>
        public int Temperature { get; set; }

        /// <summary>
        /// Top P
        /// </summary>
        public int Top_P { get; set; }
    }
}
