package io.github.zero88.exceptions;

public final class InvalidUrlException extends RuntimeErrorCodeException {

    public InvalidUrlException(String message, Throwable e) {
        super(ErrorCode.URL_ERROR, message, e);
    }

    public InvalidUrlException(String message) {
        this(message, null);
    }

    public InvalidUrlException(Throwable e) {
        this(null, e);
    }

}
