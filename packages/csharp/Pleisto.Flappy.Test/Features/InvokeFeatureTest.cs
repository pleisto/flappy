using NUnit.Framework;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Interfaces;
using Pleisto.Flappy.Utils;

namespace Pleisto.Flappy.Tests.Features
{
    /// <summary>
    /// Invoke Feature test
    /// </summary>
    public class InvokeFeatureTest :
        FeatureTestBase<
            InvokeFeature<EasyPayload<string>, EasyPayload<string>, FlappyFeatureOption>,
            InvokeFeatureDefinition<EasyPayload<string>, EasyPayload<string>>
            >
    {
        /// <summary>
        /// Test invoke feature resolve call
        /// </summary>
        /// <returns></returns>
        [Test]
        public async Task InvokeFeatureCallTestAsync()
        {
            var feature = CreateFeature();
            feature.Define.Resolve = new(TestResolve);

            var result = await feature.Feature.Call(null, "testData123");
            Assert.That(result?.Payload, Is.EqualTo("testData123"));
        }

        /// <summary>
        /// Test invoke feature system call
        /// </summary>
        /// <returns></returns>
        [Test]
        public async Task InvokeFeatureSystemCallTestAsync()
        {
            var feature = CreateFeature();
            feature.Define.Resolve = new(TestResolve);

            var result = await (feature.Feature as IFlappyFeature).SystemCall(null, new EasyPayload<string> { Payload = "testData123" });
            Assert.That(result, Is.EqualTo(new EasyPayload<string> { Payload = "testData123" }));
        }

        private static Task<EasyPayload<string>> TestResolve(EasyPayload<string> payload)
        {
            return Task.FromResult((EasyPayload<string>)payload.Payload);
        }
    }
}
