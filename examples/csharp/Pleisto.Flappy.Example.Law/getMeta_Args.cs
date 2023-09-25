using Newtonsoft.Json.Linq;
using System.ComponentModel;

namespace Pleisto.Flappy.Test.Law
{
  internal class getMeta_Args
  {
    [Description("Lawsuit full text.")]
    public string lawsuit { get; set; }

    public override string ToString()
    {
      return JObject.FromObject(this).ToString();
    }
  }
}
