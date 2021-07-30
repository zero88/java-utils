package io.github.zero88.exceptions;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RuntimeErrorCodeException extends RuntimeException implements ErrorCodeException {

    @Include
    private final ErrorCode errorCode;

    public RuntimeErrorCodeException(ErrorCode code, String message, Throwable e) {
        super(message, e);
        this.errorCode = code;
    }

    public RuntimeErrorCodeException(ErrorCode code, String message) {this(code, message, null);}

    public RuntimeErrorCodeException(ErrorCode code, Throwable e)    {this(code, null, e);}

    public RuntimeErrorCodeException(String message, Throwable e)    {this(ErrorCode.UNKNOWN_ERROR, message, e);}

    public RuntimeErrorCodeException(String message)                 {this(message, null);}

    public RuntimeErrorCodeException(Throwable e)                    {this(ErrorCode.UNKNOWN_ERROR, null, e);}

}
