using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;
using System;
using System.ComponentModel;

namespace Pleisto.Flappy.Test.Law
{
  [JsonObject(ItemRequired = Required.Always)]
  public class getMeta_Return
  {
    [JsonConverter(typeof(StringEnumConverter))]
    [DefaultValue(Verdict.Unknow)]
    public Verdict verdict { get; set; } = Verdict.Unknow;

    public string plaintiff { get; set; } = string.Empty;

    public string defendant { get; set; } = string.Empty;

    public string[] judgeOptions { get; set; } = Array.Empty<string>();

    public override string ToString()
    {
      return JObject.FromObject(this).ToString();
    }
  }
}
