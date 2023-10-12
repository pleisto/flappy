using System.Runtime.InteropServices;

namespace Pleisto.Flappy.CodeInterpreter
{
  /// <summary>
  /// Unmanaged Rust Dictonary Type
  /// </summary>
  [StructLayout(LayoutKind.Sequential)]
  internal struct RustDictonaryType : IDisposable
  {
    /// <summary>
    /// IntPtr of Key
    /// </summary>
    [MarshalAs(UnmanagedType.HString)]
    public IntPtr Key;

    /// <summary>
    /// IntPtr of Value
    /// </summary>
    [MarshalAs(UnmanagedType.HString)]
    public IntPtr Value;

    public RustDictonaryType(KeyValuePair<string, string> pair)
    {
      Key = Marshal.StringToHGlobalAnsi(pair.Key);
      Value = Marshal.StringToHGlobalAnsi(pair.Value);
    }

    public void Dispose()
    {
      if (Key != IntPtr.Zero)
        Marshal.FreeHGlobal(Key);
      if (Value != IntPtr.Zero)
        Marshal.FreeHGlobal(Value);
      GC.SuppressFinalize(this);
    }
  }
}
