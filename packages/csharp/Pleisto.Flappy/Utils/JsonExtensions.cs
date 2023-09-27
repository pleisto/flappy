using Newtonsoft.Json;
using System.Text;

namespace Pleisto.Flappy.Utils
{
  /// <summary>
  /// Program extensions
  /// </summary>
  internal static class JsonExtensions
  {
    /// <summary>
    /// Json convert to string with special formatting
    /// </summary>
    /// <param name="json"></param>
    /// <returns></returns>
    public static string JsonToString(this object json)
    {
      using var ms = new MemoryStream();
      using (var sw = new StreamWriter(ms, Encoding.UTF8, 512, true)
      {
        NewLine = "\n",
      })
      using (var jsonWriter = new JsonTextWriter(sw)
      {
        Indentation = 1,
        Formatting = Formatting.None,
      })
      {
        jsonSerializer.Serialize(jsonWriter, json);
        //return JsonConvert.SerializeObject(json,jsonSerializerOptions);
      }
      return Encoding.UTF8.GetString(ms.ToArray()).Trim();
    }

    /// <summary>
    /// Default jsonSerializer Settings
    /// </summary>
    internal static readonly JsonSerializer jsonSerializer = new JsonSerializer()
    {
      NullValueHandling = NullValueHandling.Ignore,
    };
  }
}
