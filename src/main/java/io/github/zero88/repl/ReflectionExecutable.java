package io.github.zero88.repl;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import io.github.zero88.exceptions.ReflectionException;

import lombok.NonNull;

/**
 * @see Executable
 */
public interface ReflectionExecutable {

    static ReflectionException handleError(@NonNull Executable executable, @NonNull ReflectiveOperationException e) {
        if (Reflections.LOGGER.isTraceEnabled()) {
            Reflections.LOGGER.trace("Cannot execute method " + executable.getName(), e);
        }
        if (e instanceof InvocationTargetException) {
            Throwable targetException = ((InvocationTargetException) e).getTargetException();
            if (targetException instanceof ReflectionException) {
                throw (ReflectionException) targetException;
            }
            if (Objects.nonNull(targetException)) {
                throw new ReflectionException(targetException);
            }
        }
        throw new ReflectionException(e);
    }

}
