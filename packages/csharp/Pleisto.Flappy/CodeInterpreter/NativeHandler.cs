using Newtonsoft.Json.Linq;
using Pleisto.Flappy.Exceptions;
using System.Diagnostics.CodeAnalysis;
using System.Runtime.InteropServices;

namespace Pleisto.Flappy.CodeInterpreter
{
    /// <summary>
    /// Code Interpreter of Rust Handler
    /// </summary>
    public static class NativeHandler
    {
        #region Import of native

        /// <summary>
        /// Eval Natvive Call Test
        /// </summary>
        /// <returns></returns>
        [DllImport("flappy_csharp_bindings", EntryPoint = "eval_native_call")]
        [SuppressMessage("Interoperability", "SYSLIB1054")]
        private static extern bool EvalNativeCall();

        /// <summary>
        /// Eval python call with Json input and Json output
        /// </summary>
        /// <param name="jsonIn"></param>
        /// <returns></returns>
        [DllImport("flappy_csharp_bindings", EntryPoint = "eval_python_code_by_json")]
        [SuppressMessage("Interoperability", "SYSLIB1054")]
        private static extern IntPtr EvalPythonCodeResultJson(IntPtr jsonIn);

        #endregion Import of native

        /// <summary>
        /// Native call test
        /// </summary>
        /// <returns></returns>
        public static bool NativeCall()
        {
            return EvalNativeCall();
        }

        /// <summary>
        /// Free a ptr
        /// </summary>
        /// <param name="ptr"></param>
        internal static void Free(IntPtr ptr)
        {
            if (ptr != IntPtr.Zero)
                try
                {
                    Marshal.FreeHGlobal(ptr);
                }
                catch (Exception ex)
                {
                    throw new InvalidProgramException("unable to free ptr", ex);
                }
        }

        /// <summary>
        /// Call python sandbox execute
        /// </summary>
        /// <param name="code">Python code</param>
        /// <param name="enableNetwork">Enable network for python</param>
        /// <param name="env">environment</param>
        /// <param name="cacheDir">cache directory</param>
        /// <returns></returns>
        public static NativeResult EvalPythonCode(string code, bool enableNetwork, Dictionary<string, string> env, string cacheDir = null)
        {
            var sendTo = JObject.FromObject(new NativeInput
            {
                Code = code,
                Network = enableNetwork,
                Envs = env,
                CachePath = cacheDir ?? "tmp"
            }).ToString();

            IntPtr sendToPtr = Marshal.StringToHGlobalAnsi(sendTo);
            IntPtr result = IntPtr.Zero;
            try
            {
                result = EvalPythonCodeResultJson(sendToPtr);
                var json = JObject.Parse(Marshal.PtrToStringAnsi(result)).ToObject<NativeResult>();
                if (!string.IsNullOrWhiteSpace(json.ExceptionString))
                    throw new CodeInterpreterNativeException(json);
                else
                    return json;
            }
            finally
            {
                Free(sendToPtr);
                //Free(result);
            }
        }
    }
}
