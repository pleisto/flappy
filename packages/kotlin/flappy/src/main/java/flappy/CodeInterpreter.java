package flappy;

public class CodeInterpreter {
  static {
    System.loadLibrary("flappy_java_bindings");
  }

  public static native String evalPythonCode(CodeInterpreter callback, String code);

  public static void main(String[] args) {
    CodeInterpreter.evalPythonCode(new CodeInterpreter(), "foo");
  }

  public void asyncCallback(int progress) {
    System.out.println("asyncCallback: thread id = " + progress);
  }
}
