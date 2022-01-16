package io.github.zero88.exceptions;

import org.jetbrains.annotations.NotNull;

public class HiddenException extends RuntimeErrorCodeException {

    public HiddenException(ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public HiddenException(String message, Throwable e) {
        this(ErrorCode.HIDDEN, message, e);
    }

    public HiddenException(String message) {
        this(message, null);
    }

    public HiddenException(Throwable e) {
        this(null, e);
    }

    public HiddenException(@NotNull RuntimeErrorCodeException e) {
        this(e.errorCode(), e.getMessage(), e);
    }

}
