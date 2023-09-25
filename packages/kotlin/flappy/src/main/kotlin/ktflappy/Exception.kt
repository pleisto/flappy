package flappy

class RepairableException(message: String) : Exception(message)
class NonRepairableException(message: String) : Exception(message)


class CompileException(message: String) : Exception(message)

class FatalException(message: String) : Exception(message)

class RuntimeException(message: String) : Exception(message)

class ParseException(message: String) : Exception(message)