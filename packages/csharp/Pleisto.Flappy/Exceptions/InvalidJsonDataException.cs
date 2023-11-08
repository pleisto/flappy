namespace Pleisto.Flappy.Exceptions
{
    /// <summary>
    /// Invalid Json data exception
    /// </summary>
    public class InvalidJsonDataException : Exception
    {
        /// <summary>
        /// Unable to parse json data
        /// </summary>
        /// <param name="startIdx"></param>
        /// <param name="endIdx"></param>
        /// <param name="raw"></param>
        /// <param name="splitted"></param>
        /// <param name="innerException"></param>
        internal InvalidJsonDataException(int startIdx, int endIdx, string raw, string splitted, Exception innerException)
          : base($"unable to parse json, from:{startIdx} to:{endIdx}{Environment.NewLine}raw={Environment.NewLine}{raw}{Environment.NewLine}splitted={Environment.NewLine}{splitted}",innerException)
        {
            StartIndex = startIdx;
            EndIndex = endIdx;
            Raw = raw;
            Splitted = splitted;
        }

        /// <summary>
        /// Unable to locate json data
        /// </summary>
        /// <param name="startIdx"></param>
        /// <param name="endIdx"></param>
        /// <param name="raw"></param>
        /// <param name="innerException"></param>
        internal InvalidJsonDataException(int startIdx, int endIdx, string raw, Exception innerException = null)
          : base($"unable to locate json, startIndex={startIdx} endIndex={endIdx} raw={raw}", innerException)
        {
            StartIndex = startIdx;
            EndIndex = endIdx;
            Raw = raw;
        }

        /// <summary>
        /// Start of index
        /// </summary>
        public int StartIndex { get; private set; }

        /// <summary>
        /// End of index
        /// </summary>
        public int EndIndex { get; private set; }

        /// <summary>
        /// Input json string
        /// </summary>
        public string Raw { get; private set; }

        /// <summary>
        /// Splitted json string
        /// </summary>
        public string Splitted { get; private set; }
    }
}
