package flappy

import flappy.features.FlappyCodeInterpreter
import kotlin.test.Test
import kotlin.test.assertEquals

class CodeInterpreterTest {

  @Test
  fun desc() {
    assertEquals(
      FlappyCodeInterpreter(true).buildDescription(),
      """An safe sandbox that only support the built-in library. The execution time is limited to 120 seconds. The task is to define a function named "main" that doesn't take any parameters. The output should be a String. Network access is enabled"""
    )

    assertEquals(
      FlappyCodeInterpreter(false).buildDescription(),
      """An safe sandbox that only support the built-in library. The execution time is limited to 120 seconds. The task is to define a function named "main" that doesn't take any parameters. The output should be a String. Network access is disabled"""
    )
  }
}
