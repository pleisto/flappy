package com.pleisto;

/**
 * An FlappyException encapsulates the error of an operation. This exception
 * type is used to describe an internal error from the native opendal library.
 */
public class FlappyException extends RuntimeException {
    private final Code code;

    /**
     * Construct an FlappyException. This constructor is called from native code.
     *
     * @param code string representation of the error code
     * @param message error message
     */
    @SuppressWarnings("unused")
    public FlappyException(String code, String message) {
        this(Code.valueOf(code), message);
    }

    public FlappyException(Code code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Get the error code returned from OpenDAL.
     *
     * @return The error code reported by OpenDAL.
     */
    public Code getCode() {
        return code;
    }

    /**
     * Enumerate all kinds of Error that OpenDAL may return.
     *
     * <p>
     * Read the document of
     * <a href="https://docs.rs/opendal/latest/opendal/enum.ErrorKind.html">opendal::ErrorKind</a>
     * for the details of each code.
     */
    public enum Code {
        Unexpected,
        Unsupported,
        ConfigInvalid,
        NotFound,
        PermissionDenied,
        IsADirectory,
        NotADirectory,
        AlreadyExists,
        RateLimited,
        IsSameFile,
        ConditionNotMatch,
        ContentTruncated,
        ContentIncomplete,
        InvalidInput,
    }
}
