package io.github.zero88.exceptions;

import org.jetbrains.annotations.NotNull;

public interface ErrorCodeException {

    /**
     * Return error code
     *
     * @return error code
     * @see ErrorCode
     */
    @NotNull ErrorCode errorCode();

    /**
     * Return error message
     *
     * @return error message
     * @see Exception#getMessage()
     */
    String getMessage();

    /**
     * Return cause
     *
     * @return cause
     * @see Exception#getCause()
     */
    Throwable getCause();

}
