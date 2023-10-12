using Newtonsoft.Json.Schema;
using System.Text;

namespace Pleisto.Flappy.Exceptions
{
  /// <summary>
  /// Invalid json with schema validate
  /// </summary>
  public class InvalidJsonWithSchemaValidationException : Exception
  {
    internal InvalidJsonWithSchemaValidationException(IEnumerable<ValidationError> errors)
      : base($"Json Schema is invalid")
    {
      Errors = errors;
    }

    /// <summary>
    /// Json Schema Errors
    /// </summary>
    public IEnumerable<ValidationError> Errors { get; }

    /// <summary>
    /// A string to show the errors
    /// </summary>
    /// <returns></returns>
    public override string ToString()
    {
      StringBuilder builder = new StringBuilder();
      builder.AppendLine(Message);
      builder.AppendLine("Errors of jsonschema:");
      foreach (var b in Errors)
        builder.AppendLine(Errors.ToString());
      builder.AppendLine();
      builder.AppendLine(base.ToString());

      return builder.ToString();
    }
  }
}
