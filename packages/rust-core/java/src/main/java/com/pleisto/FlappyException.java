package com.pleisto;

public class FlappyException extends RuntimeException {
    private final Code code;

    @SuppressWarnings("unused")
    public FlappyException(String code, String message) {
        this(Code.valueOf(code), message);
    }

    public FlappyException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public enum Code {
        Unexpected,
        Unsupported,
    }
}
