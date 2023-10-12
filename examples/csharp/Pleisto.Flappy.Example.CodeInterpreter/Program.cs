using Newtonsoft.Json.Linq;
using Pleisto.Flappy.CodeInterpreter;
using Pleisto.Flappy.Example.CodeInterpreter.Properties;

NativeHandler.AfterNativeCodeCall += RustCall_AfterNativeCodeCall;
NativeHandler.OnNavtiveCodeCall += RustCall_OnNavtiveCodeCall;

void RustCall_OnNavtiveCodeCall(object? sender, string e)
{
  Console.WriteLine($"On native code call");
}

void RustCall_AfterNativeCodeCall(object? sender, RustStdOutputManaged e)
{
  Console.WriteLine($"After native code call");
}

try
{
  Console.WriteLine(">>Running");
  var result = NativeHandler.EvalPythonCode(Resources.python_py, false, new Dictionary<string, string>
  {
    ["123123123"] = "1231231",
    ["2123123123"] = "1231231"
  });
  Console.WriteLine(">>Finished ");
  Console.WriteLine(JObject.FromObject(result).ToString());
}
catch (Exception ex)
{
  Console.Error.WriteLine(ex.ToString());
}
