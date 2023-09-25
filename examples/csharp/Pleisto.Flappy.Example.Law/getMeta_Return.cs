using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;
using System.ComponentModel;

namespace Pleisto.Flappy.Test.Law
{
  public class getMeta_Return
  {
    [JsonRequired]
    [JsonConverter(typeof(StringEnumConverter))]
    [DefaultValue(Verdict.Unknow)]
    public Verdict verdict { get; set; } = Verdict.Unknow;

    [JsonRequired]
    public string plaintiff { get; set; } = string.Empty;

    [JsonRequired]
    public string defendant { get; set; } = string.Empty;

    [JsonRequired]
    public string[] judgeOptions { get; set; } = Array.Empty<string>();

    public override string ToString()
    {
      return JObject.FromObject(this).ToString();
    }
  }
}
