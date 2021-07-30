package io.github.zero88.exceptions;

public class ReflectionException extends RuntimeErrorCodeException {

    public ReflectionException(String message, Throwable e) {
        super(ErrorCode.REFLECTION_ERROR, message, e);
    }

    public ReflectionException(String message) {
        this(message, null);
    }

    public ReflectionException(Throwable e) {
        this(null, e);
    }

}
