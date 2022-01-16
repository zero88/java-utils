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

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RuntimeErrorCodeException)) {
            return false;
        }
        final RuntimeErrorCodeException other = (RuntimeErrorCodeException) o;
        if (!other.canEqual(this)) {
            return false;
        }
        final Object this$errorCode = this.errorCode();
        final Object other$errorCode = other.errorCode();
        return Objects.equals(this$errorCode, other$errorCode);
    }

    protected boolean canEqual(final Object other) {return other instanceof RuntimeErrorCodeException;}

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $errorCode = this.errorCode();
        result = result * PRIME + ($errorCode == null ? 43 : $errorCode.hashCode());
        return result;
    }

}
