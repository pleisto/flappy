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
    /// <param name="splited"></param>
    /// <param name="innerException"></param>
    internal InvalidJsonDataException(int startIdx, int endIdx, string raw, string splited, Exception innerException)
      : base($"unable to parse json array, from:{startIdx} to:{endIdx} raw={raw} splited={splited}")
    {
      StartIndex = startIdx;
      EndIndex = endIdx;
      Raw = raw;
      Splited = splited;
    }

    /// <summary>
    /// Unable to locate json data
    /// </summary>
    /// <param name="startIdx"></param>
    /// <param name="endIdx"></param>
    /// <param name="raw"></param>
    /// <param name="innerException"></param>
    internal InvalidJsonDataException(int startIdx, int endIdx, string raw, Exception innerException = null)
      : base($"unable to locate json data, startIndex={startIdx} endIndex={endIdx} raw={raw}", innerException)
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
    /// Splited json string
    /// </summary>
    public string Splited { get; private set; }
  }
}
