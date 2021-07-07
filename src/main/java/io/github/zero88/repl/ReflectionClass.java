package io.github.zero88.repl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.exceptions.ReflectionException;
import io.github.zero88.utils.Functions.Silencer;
import io.github.zero88.utils.Strings;

import lombok.NonNull;

@SuppressWarnings("unchecked")
public final class ReflectionClass {

    /**
     * @param childClass Given child {@code Class}
     * @param superClass Give super {@code Class}
     * @return {@code true} if {@code childClass} is primitive class or class that sub of {@code superClass}
     * @see Class#isAssignableFrom(Class)
     */
    public static boolean assertDataType(@NonNull Class<?> childClass, @NonNull Class<?> superClass) {
        if (childClass.isPrimitive() && superClass.isPrimitive()) {
            return childClass == superClass;
        }
        if (childClass.isPrimitive()) {
            Class<?> superPrimitiveClass = getPrimitiveClass(superClass);
            return childClass == superPrimitiveClass;
        }
        if (superClass.isPrimitive()) {
            Class<?> childPrimitiveClass = getPrimitiveClass(childClass);
            return childPrimitiveClass == superClass;
        }
        return superClass.isAssignableFrom(childClass);
    }

    public static boolean isSystemClass(String clazzName) {
        return belongsTo(clazzName, "java", "javax", "sun", "com.sun");
    }

    public static boolean belongsTo(@NonNull String clazzName, String... packageNames) {
        return Arrays.stream(packageNames).map(p -> p + ".").anyMatch(clazzName::startsWith);
    }

    public static boolean isJavaLangObject(@NonNull Class<?> clazz) {
        return clazz.isPrimitive() || clazz.isEnum() || "java.lang".equals(clazz.getPackage().getName());
    }

    private static <T> Class<?> getPrimitiveClass(@NonNull Class<T> findClazz) {
        try {
            Field t = findClazz.getField("TYPE");
            if (!Reflections.hasModifiers(Modifier.PUBLIC, Modifier.STATIC).test(t)) {
                return null;
            }
            Object primitiveClazz = t.get(null);
            if (primitiveClazz instanceof Class) {
                return (Class<?>) primitiveClazz;
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Reflections.LOGGER.trace("Try casting primitive class from class " + findClazz.getName(), e);
        }
        return null;
    }

    public static <T> Stream<Class<T>> stream(String packageName, Class<T> parentCls) {
        return stream(packageName, parentCls, clazz -> true);
    }

    /**
     * Scan all classes in given package that matches annotation and sub class given parent class.
     *
     * @param <T>             Type of output
     * @param packageName     Given package name
     * @param parentCls       Given parent class. May {@code interface} class, {@code abstract} class or {@code null} if
     *                        none inherited
     * @param annotationClass Given annotation type class {@code @Target(ElementType.TYPE_USE)}
     * @return List of matching class
     */
    public static <T> Stream<Class<T>> stream(String packageName, Class<T> parentCls,
                                              @NonNull Class<? extends Annotation> annotationClass) {
        return stream(packageName, parentCls, Reflections.hasAnnotation(annotationClass));
    }

    public static <T> Stream<Class<T>> stream(String pkgName, Class<T> parentCls, @NonNull Predicate<Class<?>> filter) {
        return (Stream<Class<T>>) Reflections.loadScanner()
                                             .classStream(pkgName, filter.and(cls -> assertDataType(cls, parentCls)))
                                             .map(cls -> (T) cls);
    }

    public static <T> Class<T> findClass(String clazz) {
        try {
            return (Class<T>) Class.forName(Strings.requireNotBlank(clazz), true, Reflections.contextClassLoader());
        } catch (ClassNotFoundException | ClassCastException e) {
            Reflections.LOGGER.trace("Not found class [" + clazz + "]", e);
            return null;
        }
    }

    public static <T> T createObject(String clazz) {
        final Class<Object> aClass = findClass(clazz);
        if (Objects.isNull(aClass)) {
            return null;
        }
        return (T) createObject(aClass);
    }

    public static <T> T createObject(String clazz, @NonNull Arguments arguments) {
        final Class<Object> aClass = findClass(clazz);
        if (Objects.isNull(aClass)) {
            return null;
        }
        return (T) createObject(aClass, arguments);
    }

    public static <T> T createObject(Class<T> clazz) {
        return createObject(clazz, new Silencer<>()).get();
    }

    public static <T> T createObject(Class<T> clazz, Arguments arguments) {
        return createObject(clazz, arguments, new Silencer<>()).get();
    }

    public static <T> Silencer<T> createObject(Class<T> clazz, Silencer<T> silencer) {
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            silencer.accept(constructor.newInstance(), null);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            silencer.accept(null, new HiddenException(
                new ReflectionException("Cannot init instance of " + clazz.getName(), e)));
        }
        return silencer;
    }

    public static <T> Silencer<T> createObject(@NonNull Class<T> clazz, @NonNull Arguments arguments,
                                               @NonNull Silencer<T> silencer) {
        try {
            silencer.accept(createObject(clazz, arguments.argClasses(), arguments.argValues()), null);
        } catch (ReflectiveOperationException e) {
            silencer.accept(null, new HiddenException(
                new ReflectionException("Cannot init instance of " + clazz.getName(), e)));
        }
        return silencer;
    }

    private static <T> T createObject(@NonNull Class<T> clazz, Class<?>[] classes, Object[] args)
        throws ReflectiveOperationException {
        final Constructor<T> constructor = clazz.getDeclaredConstructor(classes);
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

}
