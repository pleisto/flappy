using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;
using System.ComponentModel;

namespace Pleisto.Flappy.Test.Law
{
  [JsonObject(ItemRequired = Required.Always)]
  public class GetMeta_Return
  {
    [JsonConverter(typeof(StringEnumConverter))]
    [DefaultValue(Verdict.Unknow)]
    public Verdict Verdict { get; set; } = Verdict.Unknow;

    public string Plaintiff { get; set; } = string.Empty;

    public string Defendant { get; set; } = string.Empty;

    public string[] JudgeOptions { get; set; } = Array.Empty<string>();

    public override string ToString()
    {
      return JObject.FromObject(this).ToString();
    }
  }
}
