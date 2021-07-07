package io.github.zero88.repl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.zero88.exceptions.ReflectionException;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;

import lombok.NonNull;

@SuppressWarnings("unchecked")
public final class ReflectionField implements ReflectionExecutable {

    /**
     * Find declared fields in given {@code class} that matches with filter
     *
     * @param clazz     Given {@code class} to find methods
     * @param predicate Given predicate
     * @return Stream of matching {@code fields}
     */
    public static Stream<Field> stream(@NonNull Class<?> clazz, Predicate<Field> predicate) {
        Stream<Field> stream = Stream.of(clazz.getDeclaredFields());
        if (Objects.nonNull(predicate)) {
            return stream.filter(predicate);
        }
        return stream;
    }

    public static List<Field> find(@NonNull Class<?> clazz, Predicate<Field> predicate) {
        return stream(clazz, predicate).collect(Collectors.toList());
    }

    public static <T> T constantByName(@NonNull Class<?> clazz, String name) {
        Predicate<Field> filter = Functions.and(Reflections.predicateConstant(),
                                                f -> f.getName().equals(Strings.requireNotBlank(name)));
        return (T) stream(clazz, filter).map(field -> getConstant(clazz, field)).findFirst().orElse(null);
    }

    public static <T> List<T> getConstants(@NonNull Class<?> clazz, @NonNull Class<T> fieldClass) {
        return streamConstants(clazz, fieldClass).collect(Collectors.toList());
    }

    public static <T> List<T> getConstants(@NonNull Class<?> clazz, @NonNull Class<T> fieldClass,
                                           Predicate<Field> predicate) {
        return streamConstants(clazz, fieldClass, predicate).collect(Collectors.toList());
    }

    public static <T> Stream<T> streamConstants(@NonNull Class<T> clazz) {
        return streamConstants(clazz, clazz, null);
    }

    public static <T> Stream<T> streamConstants(@NonNull Class<?> clazz, @NonNull Class<T> fieldClass) {
        return streamConstants(clazz, fieldClass, null);
    }

    public static <T> Stream<T> streamConstants(@NonNull Class<?> clazz, @NonNull Class<T> fieldClass,
                                                Predicate<Field> predicate) {
        Predicate<Field> filter = Functions.and(Reflections.predicateConstant(),
                                                f -> ReflectionClass.assertDataType(fieldClass, f.getType()));
        if (Objects.nonNull(predicate)) {
            filter = filter.and(predicate);
        }
        return stream(clazz, filter).map(field -> getConstant(clazz, field));
    }

    public static <T> T getConstant(@NonNull Class<?> clazz, Field field) {
        try {
            return (T) field.get(null);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new ReflectionException(
                Strings.format("Failed to get field constant {0} of {1}", field.getName(), clazz.getName()), e);
        }
    }

    public static <T> T getConstant(@NonNull Class<?> clazz, Field field, T fallback) {
        try {
            return (T) field.get(null);
        } catch (IllegalAccessException | ClassCastException e) {
            if (Reflections.LOGGER.isTraceEnabled()) {
                Reflections.LOGGER.trace("Failed to get field constant {} of {}", field.getName(), clazz.getName(), e);
            }
            return fallback;
        }
    }

    public static <T> List<T> getFieldValuesByType(@NonNull Object obj, @NonNull Class<T> searchType) {
        Predicate<Field> predicate = Functions.and(Reflections.notModifiers(Modifier.STATIC),
                                                   f -> ReflectionClass.assertDataType(f.getType(), searchType));
        return stream(obj.getClass(), predicate).map(f -> getFieldValue(obj, f, searchType))
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList());
    }

    public static <T> T getFieldValue(@NonNull Object obj, @NonNull Field f, @NonNull Class<T> type) {
        try {
            f.setAccessible(true);
            return type.cast(f.get(obj));
        } catch (IllegalAccessException | ClassCastException e) {
            Reflections.LOGGER.trace("Cannot get data of field " + f.getName(), e);
            return null;
        }
    }

}
