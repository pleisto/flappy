using Newtonsoft.Json.Linq;
using NUnit.Framework;
using Pleisto.Flappy.Features.Invoke;
using Pleisto.Flappy.Interfaces;

namespace Pleisto.Flappy.Tests
{
    public class Agent
    {
        [Test]
        public async Task CallFeatureTestAsync()
        {
            const string callName = "testCall";
            const string callResult = "validConfigure";
            var agent = new FlappyAgent(new FlappyAgentConfig
            {
                Features = new IFlappyFeature[]
                 {
                     new InvokeFeature<string, string, FlappyFeatureOption>(new InvokeFeatureDefinition<string, string>
                     {
                         Name = callName,
                         Args = string.Empty,
                         Description = string.Empty,
                         ReturnType = string.Empty,
                         Resolve = new FlappyAgent.ResolveFeature<string, string>(i => Task.FromResult(i))
                     })
                 }
            }, null, null, null);
            var result = await agent.CallFeature(callName, callResult) as string;
            if (result == null)
                Assert.Fail("result is null!");
            Assert.That(result, Is.EqualTo(callResult));
        }
    }
}
