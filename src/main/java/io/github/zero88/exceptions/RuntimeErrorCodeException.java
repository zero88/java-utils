package io.github.zero88.exceptions;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class RuntimeErrorCodeException extends RuntimeException implements ErrorCodeException {

    private final ErrorCode errorCode;

    public RuntimeErrorCodeException(@NotNull ErrorCode code, String message, Throwable e) {
        super(message, e);
        this.errorCode = Objects.requireNonNull(code, "Error code is required");
    }

    public RuntimeErrorCodeException(ErrorCode code, String message) {this(code, message, null);}

    public RuntimeErrorCodeException(ErrorCode code, Throwable e)    {this(code, null, e);}

    public RuntimeErrorCodeException(String message, Throwable e)    {this(ErrorCode.UNKNOWN_ERROR, message, e);}

    public RuntimeErrorCodeException(String message)                 {this(message, null);}

    public RuntimeErrorCodeException(Throwable e)                    {this(ErrorCode.UNKNOWN_ERROR, null, e);}

    protected RuntimeErrorCodeException(ErrorCode errorCode)         {this(errorCode, null, null);}

    public @NotNull ErrorCode errorCode() {
        return this.errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RuntimeErrorCodeException that = (RuntimeErrorCodeException) o;
        return errorCode.equals(that.errorCode);
    }

    @Override
    public int hashCode() {
        return errorCode.hashCode();
    }

}
