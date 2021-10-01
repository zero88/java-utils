package io.github.zero88.repl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

public interface ReflectionScanner {

    default Stream<? extends Class<?>> classStream(String packageName, Predicate<Class<?>> classPredicate) {
        return classStream(packageName, false, classPredicate);
    }

    Stream<? extends Class<?>> classStream(String packageName, boolean recursive, Predicate<Class<?>> classPredicate);

    <T> Stream<Constructor<T>> constructorStream(@NotNull Class<T> cls, Predicate<Constructor<T>> constructorPredicate);

    Stream<Method> methodStream(@NotNull Class<?> cls, Predicate<Method> methodPredicate);

    Stream<Field> fieldStream(@NotNull Class<?> cls, Predicate<Field> fieldPredicate);

}
