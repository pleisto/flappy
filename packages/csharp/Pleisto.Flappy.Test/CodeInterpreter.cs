using Newtonsoft.Json.Linq;
using NUnit.Framework;
using Pleisto.Flappy.CodeInterpreter;

namespace Pleisto.Flappy.Tests
{
    /// <summary>
    /// Test of CodeInterpreter
    /// </summary>
    public class CodeInterpreter
    {
        /// <summary>
        /// Test Result of python helloworld
        /// </summary>
        [Test]
        public void PythonHelloWorldTest()
        {
            const string pythonHelloWorld = @"
print('Hello World');
";
            var result = NativeHandler.EvalPythonCode(pythonHelloWorld, false, new Dictionary<string, string>
            {
            });
            Console.WriteLine(JObject.FromObject(result).ToString());
            Assert.That(result.StdOut.Trim(), Is.EqualTo("Hello World"));
        }

        /// <summary>
        /// Test of environment
        /// </summary>
        [Test]
        public void PythonEnvironment()
        {
            const string env = "this_is_env_test_success";
            const string pythonEnvironment = @"
import os

print(os.environ['testEnv'])
";
            var result = NativeHandler.EvalPythonCode(pythonEnvironment, false, new Dictionary<string, string>
            {
                ["testEnv"] = env
            });
            Console.WriteLine(JObject.FromObject(result).ToString());
            Assert.That(result.StdOut.Trim(), Is.EqualTo(env));
        }

        /// <summary>
        /// Test of native call lib
        /// </summary>
        [Test]
        public void NativeCall()
        {
            Assert.That(NativeHandler.NativeCall(), Is.EqualTo(true));
        }
    }
}
