using System.Runtime.InteropServices;

namespace Pleisto.Flappy.CodeInterpreter
{
  /// <summary>
  /// Code Interpreter of Rust Handler
  /// </summary>
  public static class NativeHandler
  {
    static NativeHandler()
    {
    }

    #region Import of native

    /// <summary>
    /// P/Invoke for Rust Code Call
    /// </summary>
    /// <param name="code">Python code</param>
    /// <param name="enableNetwork">Enable network for python</param>
    /// <param name="env">environment</param>
    /// <param name="envSize"></param>
    /// <param name="cacheDir">cache directory</param>
    /// <returns></returns>
    [DllImport("flappy_csharp_bindings", EntryPoint = "eval_python_code")]
    private static extern RustStdOutput EvalPythonCodeRust(IntPtr code, bool enableNetwork, IntPtr env, uint envSize, IntPtr cacheDir);

    /// <summary>
    /// Eval Natvive Call Test
    /// </summary>
    /// <returns></returns>
    [DllImport("flappy_csharp_bindings", EntryPoint = "eval_native_call")]
    private static extern bool EvalNativeCall();

    #endregion Import of native

    /// <summary>
    /// Native call test
    /// </summary>
    /// <returns></returns>
    public static bool NativeCall()
    {
      return EvalNativeCall();
    }

    internal static void Free(IntPtr code)
    {
      return;
      if (code != IntPtr.Zero)
        Marshal.FreeHGlobal(code);
    }

    /// <summary>
    /// Call python sandbox execute
    /// </summary>
    /// <param name="code">Python code</param>
    /// <param name="enableNetwork">Enable network for python</param>
    /// <param name="env">environment</param>
    /// <param name="cacheDir">cache directory</param>
    /// <returns></returns>
    public static RustStdOutputManaged EvalPythonCode(string code, bool enableNetwork, Dictionary<string, string> env, string cacheDir = null)
    {
      var directoryTypesArray = (from b in env ?? new Dictionary<string, string>()
                                 select new RustDictonaryType(b)).ToArray();

      var dictonaryTypes = Marshal.UnsafeAddrOfPinnedArrayElement(directoryTypesArray, 0);
      cacheDir ??= "code-interpreter";
      if (!Directory.Exists(cacheDir))
        Directory.CreateDirectory(cacheDir);
      try
      {
        OnNavtiveCodeCall?.Invoke(null, code);
        using var result = EvalPythonCodeRust(Marshal.StringToHGlobalAnsi(code.Replace("\r\n", "\n")), enableNetwork, dictonaryTypes, (uint)env.Count, Marshal.StringToHGlobalAnsi(cacheDir));

        var ret = new RustStdOutputManaged(result);
        AfterNativeCodeCall?.Invoke(null, ret);
        return ret;
      }
      finally
      {
        foreach (var b in directoryTypesArray)
          b.Dispose();
        Free(dictonaryTypes);
      }
    }

    /// <summary>
    /// On native code call event
    /// </summary>
    public static event EventHandler<string> OnNavtiveCodeCall;

    /// <summary>
    /// After native code call
    /// </summary>
    public static event EventHandler<RustStdOutputManaged> AfterNativeCodeCall;
  }
}
