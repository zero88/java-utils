package io.github.zero88.exceptions;

public class FileException extends RuntimeErrorCodeException {
    
    protected FileException(ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public FileException(String message, Throwable e) {
        this(ErrorCode.FILE_ERROR, message, e);
    }

    public FileException(String message) {
        this(message, null);
    }

    public FileException(Throwable e) {
        this(null, e);
    }

}
