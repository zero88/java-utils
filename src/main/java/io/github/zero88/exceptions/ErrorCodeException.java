package io.github.zero88.exceptions;

public interface ErrorCodeException {

    /**
     * Return error code
     *
     * @return error code
     * @see ErrorCode
     */
    ErrorCode errorCode();

    /**
     * Return error message
     *
     * @return error message
     * @see Exception#getMessage()
     */
    String getMessage();

    /**
     * Return cause
     * @return cause
     * @see Exception#getCause()
     */
    Throwable getCause();
}
