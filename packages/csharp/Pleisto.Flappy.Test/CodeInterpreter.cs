using Newtonsoft.Json.Linq;
using Pleisto.Flappy.CodeInterpreter;

namespace Pleisto.Flappy.Test
{
  public class CodeInterpreter
  {
    private const string pythonHelloWorld = @"
print('Hello World');
";

    [Test]
    public void PythonHelloWorldTest()
    {
      Console.WriteLine(">>Running");
      var result = NativeHandler.EvalPythonCode(pythonHelloWorld, false, new Dictionary<string, string>
      {
        ["123123123"] = "1231231",
        ["2123123123"] = "1231231"
      });
      Console.WriteLine(">>Finished ");
      Console.WriteLine(JObject.FromObject(result).ToString());
    }
  }
}
