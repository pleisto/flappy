using Newtonsoft.Json.Linq;
using System.ComponentModel;

namespace Pleisto.Flappy.Test.Law
{
  internal class getLatestLawsuits_Args
  {
    public string plantiff { get; set; }

    [Description("For demo purpose. set to False")]
    public bool arg1 { get; set; }

    [Description("ignore it")]
    public bool arg2 { get; set; }

    public override string ToString()
    {
      return JObject.FromObject(this).ToString();
    }
  }
}
