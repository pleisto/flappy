package flappy;

import org.junit.jupiter.api.Test;

import static flappy.FieldKt.buildFieldProperties;
import static flappy.JsonKt.asString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchemaTestJava {
  @Test
  public void schemaTest() {
    assertEquals(
      asString(buildFieldProperties(String.class)), """
        {"type":"string"}
        """.trim()
    );
  }

  @Test
  public void fail() {
//    buildFieldProperties(list)
  }

}
