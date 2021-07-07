package io.github.zero88.repl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class SimpleScanner implements ReflectionScanner {

    @Override
    public Stream<Class<?>> classStream(String pkgName, Predicate<Class<?>> classPredicate) {
        InputStream is = Reflections.contextClassLoader().getResourceAsStream(pkgName.replaceAll("[.]", "/"));
        if (Objects.isNull(is)) {
            return Stream.empty();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return reader.lines()
                     .filter(line -> line.endsWith(".class"))
                     .map(c -> ReflectionClass.findClass(pkgName + "." + c.substring(0, c.lastIndexOf('.'))));
    }

    @Override
    public <T> Stream<Constructor<T>> constructorStream(Class<T> cls, Predicate<Constructor<T>> constructorPredicate) {
        return Arrays.stream(cls.getDeclaredConstructors())
                     .filter(c -> constructorPredicate.test((Constructor<T>) c))
                     .map(c -> (Constructor<T>) c);
    }

    @Override
    public Stream<Method> methodStream(Class<?> cls, Predicate<Method> methodPredicate) {
        return Arrays.stream(cls.getDeclaredMethods()).filter(methodPredicate);
    }

    @Override
    public Stream<Field> fieldStream(Class<?> cls, Predicate<Field> fieldPredicate) {
        return Arrays.stream(cls.getDeclaredFields()).filter(fieldPredicate);
    }

}
