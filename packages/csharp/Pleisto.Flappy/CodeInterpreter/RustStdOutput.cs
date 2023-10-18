using System.Runtime.InteropServices;

namespace Pleisto.Flappy.CodeInterpreter
{
  /// <summary>
  /// Unmanaged Rust Output Struct
  /// </summary>
  [StructLayout(LayoutKind.Sequential)]
  internal struct RustStdOutput : IDisposable
  {
    /// <summary>
    /// StdErr
    /// </summary>
    public IntPtr StdOut;

    /// <summary>
    /// StdOut
    /// </summary>
    public IntPtr StdErr;

    /// <summary>
    /// Exception Message
    /// </summary>
    public IntPtr ErrorMessage;

    public void Dispose()
    {
      NativeHandler.Free(StdErr);
      NativeHandler.Free(StdOut);
      NativeHandler.Free(ErrorMessage);
      GC.SuppressFinalize(this);
    }
  }
}
