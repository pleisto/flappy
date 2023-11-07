using McMaster.Extensions.CommandLineUtils;
using Newtonsoft.Json.Linq;
using Pleisto.Flappy.CodeInterpreter;
using System.Diagnostics;

namespace Pleisto.Flappy.Examples.CodeInterpreter
{
  [Command("code-interpreter-native-call", Description = "Sample and test of Native Call")]
  internal class NativeCall
  {
    private const string pythonInspectCode = @"
print('Hello World');
";

    public void OnExecute()
    {
      if (NativeHandler.NativeCall())
      {
        Console.WriteLine("Native call success!");
        Stopwatch timer = new Stopwatch();
        timer.Start();
        var result = NativeHandler.EvalPythonCode(pythonInspectCode, false, new Dictionary<string, string>
        {
        });
        timer.Stop();
        Console.WriteLine($"Result of Execute:");
        Console.WriteLine(JObject.FromObject(result).ToString());
        Console.WriteLine("Elasped ms:" + timer.ElapsedMilliseconds);
      }
      else
      {
        Console.WriteLine("Native call faild!");
        return;
      }
    }
  }
}
