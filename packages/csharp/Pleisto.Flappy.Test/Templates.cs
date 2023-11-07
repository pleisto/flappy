using NUnit.Framework;
using Pleisto.Flappy.Utils;
namespace Pleisto.Flappy.Tests
{
    public class Templates
    {
        [Test]
        public void ShowEmbeddedResourcesTemplates()
        {
            Assert.Pass(string.Join(Environment.NewLine, TemplateRenderer.Templates));
        }

        [Test]
        public void ReadResource()
        {
            Assert.Pass(TemplateRenderer.GetTemplate("features.synthesized.systemMessage"));
        }

        [Test]
        public void ReadResourceWithExtension()
        {
            Assert.Pass(TemplateRenderer.GetTemplate("features.synthesized.systemMessage.mustache"));
        }

        [Test]
        public void RenderResource()
        {
            Assert.Pass(TemplateRenderer.Render("features.synthesized.systemMessage.mustache", new System.Collections.Generic.Dictionary<string, object>
            {
                ["argsSchema"] = "argsSchemaReplaced",
                ["returnTypeSchema"] = "returnTypeSchemaReplaced",
            }));
        }
    }
}
