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
    public IntPtr StdErr;

    /// <summary>
    /// StdOut
    /// </summary>
    public IntPtr StdOut;

    /// <summary>
    /// Exception Message
    /// </summary>
    public IntPtr ErrorMessage;

    public void Dispose()
    {
      /*if (StdOut != IntPtr.Zero)
      {
        Console.WriteLine($"Free Stdout");
        Marshal.FreeHGlobal(StdOut);
        StdErr = IntPtr.Zero;
      }
      if (StdOut != IntPtr.Zero)
      {
        Console.WriteLine($"Free StdErr");
        Marshal.FreeHGlobal(StdErr);
        StdOut = IntPtr.Zero;
      }
      if (ErrorMessage != IntPtr.Zero)
        try
        {
          Console.WriteLine($"Free ErrorMessage");
          Marshal.ZeroFreeCoTaskMemUTF8(ErrorMessage);

        }
        finally
        {
          ErrorMessage = IntPtr.Zero;
        }*/
      GC.SuppressFinalize(this);
    }
  }
}
