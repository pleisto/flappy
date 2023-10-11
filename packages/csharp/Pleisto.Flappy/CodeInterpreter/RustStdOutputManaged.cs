using System.Runtime.InteropServices;

namespace Pleisto.Flappy.CodeInterpreter
{
  /// <summary>
  /// Managed of Rust Result
  /// </summary>
  public class RustStdOutputManaged
  {
    /// <summary>
    /// Std Error
    /// </summary>
    public string StdErr { get; private set; }

    /// <summary>
    /// Std Out
    /// </summary>
    public string StdOut { get; private set; }

    /// <summary>
    /// Exception Message
    /// </summary>
    public string ErrorMessage { get; private set; }


    internal RustStdOutputManaged(RustStdOutput unmanaged)
    {
      if (unmanaged.StdErr != IntPtr.Zero)
        StdErr = Marshal.PtrToStringAnsi(unmanaged.StdErr);

      if (unmanaged.StdOut != IntPtr.Zero)
        StdOut = Marshal.PtrToStringAnsi(unmanaged.StdOut);

      if (unmanaged.ErrorMessage != IntPtr.Zero)
        ErrorMessage = Marshal.PtrToStringAnsi(unmanaged.ErrorMessage);
    }
  }
}
