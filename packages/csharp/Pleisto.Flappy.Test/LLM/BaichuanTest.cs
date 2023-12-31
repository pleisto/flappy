using NUnit.Framework;
using Pleisto.Flappy.LLM;

namespace Pleisto.Flappy.Tests.LLM
{
    /// <summary>
    /// Baichuan test
    /// </summary>
    public class BaichuanTest : LLMTest<Baichuan>
    {
        private string BaichuanToken => Environment.GetEnvironmentVariable("BAICHUAN_API_KEY");
        private string BaichuanSecret => Environment.GetEnvironmentVariable("BAICHUAN_API_SECRET");

        protected override Baichuan OnLLMCreate()
        {
            if (BaichuanToken == null || BaichuanSecret == null)
                Assert.Pass("SKIP!");
            return new Baichuan(null, BaichuanToken, BaichuanSecret);
        }
    }
}
