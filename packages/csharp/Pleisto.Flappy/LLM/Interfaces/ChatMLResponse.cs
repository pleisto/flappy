namespace Pleisto.Flappy.LLM.Interfaces
{
    /// <summary>
    /// LLM Response
    /// </summary>
    public class ChatMLResponse
    {
        /// <summary>
        /// Response is success
        /// </summary>
        public bool Success { get; set; }

        /// <summary>
        /// Data
        /// </summary>
        public string Data { get; set; }
    }
}
