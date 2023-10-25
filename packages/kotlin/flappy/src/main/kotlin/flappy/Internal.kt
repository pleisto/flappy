package flappy


internal fun String.toUniversal(): String = this.replace("\\n|\\r\\n", System.getProperty("line.separator"))
