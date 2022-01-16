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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionClass implements ReflectionElement {

    /**
     * @param childClass Given child {@code Class}
     * @param superClass Give super {@code Class}
     * @return {@code true} if {@code childClass} is primitive class or class that sub of {@code superClass}
     * @see #assertDataType(Class, Class)
     */
    public static boolean assertDataType(@NonNull String childClass, @NonNull Class<?> superClass) {
        return assertDataType(Objects.requireNonNull(findClass(childClass), "Not found class [" + childClass + "]"),
                              superClass);
    }

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
        return clazz.isPrimitive() || clazz.isEnum() ||
               (!clazz.isArray() && "java.lang".equals(clazz.getPackage().getName()));
    }

    /**
     * Return the java {@link java.lang.Class} object if the given class name is primitive
     *
     * @param className The class name
     * @return a primitive class or {@code null} if not primitive
     */
    public static Class<?> parsePrimitiveType(String className) {
        switch (className) {
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "char":
                return char.class;
            case "void":
                return void.class;
            default:
                return null;
        }
    }

    private static <T> Class<?> getPrimitiveClass(@NonNull Class<T> findClazz) {
        try {
            Field t = findClazz.getField("TYPE");
            if (!ReflectionMember.hasModifiers(Modifier.PUBLIC, Modifier.STATIC).test(t)) {
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

    public static <T> Stream<Class<T>> stream(String pkgName, Class<T> parentCls) {
        return stream(pkgName, parentCls, clazz -> true);
    }

    /**
     * Scan all classes in given package that matches annotation and subclasses given parent class.
     *
     * @param <T>             Type of output
     * @param pkgName         Given package name
     * @param parentCls       Given parent class. May {@code interface} class, {@code abstract} class or {@code null} if
     *                        none inherited
     * @param annotationClass Given annotation type class {@code @Target(ElementType.TYPE_USE)}
     * @return List of matching class
     */
    public static <T> Stream<Class<T>> stream(String pkgName, Class<T> parentCls,
        @NonNull Class<? extends Annotation> annotationClass) {
        return stream(pkgName, parentCls, ReflectionElement.hasAnnotation(annotationClass));
    }

    public static <T> Stream<Class<T>> stream(String pkgName, Class<T> parentCls, @NonNull Predicate<Class<T>> filter) {
        return stream(Reflections.loadScanner(), pkgName, parentCls, filter);
    }

    public static <T> Stream<Class<T>> stream(ReflectionScanner scanner, String pkgName, Class<T> parentCls,
        @NonNull Predicate<Class<T>> filter) {
        return (Stream<Class<T>>) scanner.classStream(pkgName, cls -> assertDataType(cls, parentCls))
                                         .map(cls -> (Class<T>) cls)
                                         .filter(filter)
                                         .map(cls -> (T) cls);
    }

    public static boolean hasClass(String cls) {
        return hasClass(cls, Reflections.classLoaders());
    }

    public static boolean hasClass(String cls, ClassLoader... classLoaders) {
        if (Objects.nonNull(parsePrimitiveType(cls))) {
            return true;
        }
        for (ClassLoader classLoader : classLoaders) {
            try {
                Class.forName(Objects.requireNonNull(cls), false, classLoader);
                return true;
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }
        return false;
    }

    public static <T> Class<T> findClass(String cls) {
        return findClass(cls, Reflections.classLoaders());
    }

    public static <T> Class<T> findClass(String cls, ClassLoader... classLoaders) {
        final Class<?> aClass = parsePrimitiveType(cls);
        if (Objects.nonNull(aClass)) {
            return (Class<T>) aClass;
        }
        for (ClassLoader classLoader : classLoaders) {
            try {
                return (Class<T>) Class.forName(Strings.requireNotBlank(cls), true, classLoader);
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }
        return null;
    }

    public static <T> T createObject(String clazz) {
        final Class<Object> aClass = findClass(clazz);
        return Objects.isNull(aClass) ? null : (T) createObject(aClass);
    }

    public static <T> T createObject(String clazz, @NonNull Arguments arguments) {
        final Class<Object> aClass = findClass(clazz);
        return Objects.isNull(aClass) ? null : (T) createObject(aClass, arguments);
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
            final Constructor<T> constructor = clazz.getDeclaredConstructor(arguments.argClasses());
            constructor.setAccessible(true);
            silencer.accept(constructor.newInstance(arguments.argValues()), null);
        } catch (ReflectiveOperationException e) {
            silencer.accept(null, new HiddenException(
                new ReflectionException("Cannot init instance of " + clazz.getName(), e)));
        }
        return silencer;
    }

}
