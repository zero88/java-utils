package io.github.zero88.repl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ReflectionScanner {

    Stream<Class<?>> classStream(String packageName, Predicate<Class<?>> classPredicate);

    <T> Stream<Constructor<T>> constructorStream(Class<T> cls, Predicate<Constructor<T>> constructorPredicate);

    Stream<Method> methodStream(Class<?> cls, Predicate<Method> methodPredicate);

    Stream<Field> fieldStream(Class<?> cls, Predicate<Field> fieldPredicate);

}
