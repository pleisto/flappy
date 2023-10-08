package flappy


sealed class FlappyException(message: String) : Exception(message) {

  class RepairableException(message: String) : FlappyException(message)

  class NonRepairableException(message: String) : FlappyException(message)

  class CompileException(message: String) : FlappyException(message)

  class FatalException(message: String) : FlappyException(message)
  
  class ParseException(message: String) : FlappyException(message)

  class FieldNotFoundException(message: String) : FlappyException(message)
}
