package com.pleisto;

public class FlappyJniException extends RuntimeException {
    private final Code code;

    @SuppressWarnings("unused")
    public FlappyJniException(String code, String message) {
        this(Code.valueOf(code), message);
    }

    public FlappyJniException(Code code, String message) {
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
