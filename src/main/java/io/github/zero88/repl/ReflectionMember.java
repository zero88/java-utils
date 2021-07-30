package io.github.zero88.repl;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import io.github.zero88.exceptions.ReflectionException;

import lombok.NonNull;

/**
 * @see Member
 */
public interface ReflectionMember {

    static <T extends Member> Predicate<T> hasModifiers(int... modifiers) {
        int searchMods = Arrays.stream(modifiers).reduce((left, right) -> left | right).orElse(0);
        return member -> (member.getModifiers() & searchMods) == searchMods;
    }

    static <T extends Member> Predicate<T> notModifiers(int... modifiers) {
        int searchMods = Arrays.stream(modifiers).reduce((left, right) -> left | right).orElse(0);
        return member -> (member.getModifiers() & searchMods) != searchMods;
    }

    /**
     * Constant means {@code public static final}
     *
     * @param <T> Type of Member
     * @return A constant predicate
     */
    static <T extends Member> Predicate<T> constantPredicate() {
        return hasModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    }

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
