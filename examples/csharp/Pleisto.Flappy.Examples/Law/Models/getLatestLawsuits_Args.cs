using Newtonsoft.Json.Linq;
using System.ComponentModel;

namespace Pleisto.Flappy.Test.Law
{
  internal class GetLatestLawsuits_Args
  {
    public string Plantiff { get; set; }

    [Description("For demo purpose. set to False")]
    public bool Arg1 { get; set; }

    [Description("ignore it")]
    public bool Arg2 { get; set; }

    public override string ToString()
    {
      return JObject.FromObject(this).ToString();
    }
  }
}
