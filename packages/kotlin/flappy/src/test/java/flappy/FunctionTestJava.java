package flappy;

import flappy.annotations.FlappyField;
import org.junit.jupiter.api.Test;

import java.util.List;

class FunctionTestJava {
  @Test
  public void functionTest() {
    FlappySynthesizedFunction sampleFunction = new FlappySynthesizedFunction(
      "getMeta",
      "Extract meta data from a lawsuit full text.",
      SampleArguments.class,
      SampleReturn.class
    );
  }

  enum Level {
    LOW,
    MEDIUM,
    HIGH
  }


  static class SampleArguments {
    @FlappyField
    private final Long longType1 = 1123L;
    @FlappyField
    long longType2;
    @FlappyField
    Integer intType1;
    @FlappyField
    Double doubleType1;
    @FlappyField
    double doubleType2;
    @FlappyField
    Float floatType1;
    @FlappyField
    float floatType2;
    @FlappyField
    private int intType2;
    @FlappyField
    private String stringType;
    @FlappyField
    private Boolean booleanType1;
    @FlappyField
    private boolean booleanType2;
    @FlappyField(subType = FieldType.STRING)
    private List<String> listString;

//        @FlappyArgsField
//        private Array<String> arrayString;

    //        @FlappyArgsField
//        private ArrayList<String> listString2;
    @FlappyField(subType = FieldType.BOOLEAN)
    private List<Boolean> listBoolean;
    @FlappyField(subType = FieldType.INTEGER)
    private List<Integer> listInteger;
    @FlappyField
    private Level level;

    @FlappyField
    private Object object;

    @FlappyField
    private Strength strength;

    enum Strength {
      Min,
      Mid,
      Max
    }
  }

  static class SampleReturn {

    @FlappyField
    private final int a = 1;
  }


}
