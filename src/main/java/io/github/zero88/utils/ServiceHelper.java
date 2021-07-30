package io.github.zero88.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.repl.Reflections;

import lombok.NonNull;

/**
 * Helper to load service in {@code META-INF/services} on classpath
 *
 * @see ServiceLoader
 */
public final class ServiceHelper {

    @Nullable
    public static <T> T loadFactory(@NonNull Class<T> clazz) {
        return Optional.ofNullable(loadFactory(clazz, Reflections.contextClassLoader()))
                       .orElseGet(() -> loadFactory(clazz, Reflections.staticClassLoader()));
    }

    @Nullable
    public static <T> T loadFactory(@NonNull Class<T> clazz, @NonNull ClassLoader classLoader) {
        ServiceLoader<T> factories = ServiceLoader.load(clazz, classLoader);
        return factories.iterator().hasNext() ? factories.iterator().next() : null;
    }

    public static <T> T loadFactoryOrThrow(@NonNull Class<T> clazz) {
        T factory = loadFactory(clazz);
        if (factory == null) {
            throw new IllegalStateException("Cannot find META-INF/services/" + clazz.getName() + " on classpath");
        }
        return factory;
    }

    public static <T> Collection<T> loadFactories(Class<T> clazz) {
        return loadFactories(clazz, null);
    }

    public static <T> Collection<T> loadFactories(Class<T> clazz, ClassLoader classLoader) {
        ServiceLoader<T> factories = classLoader != null
                                     ? ServiceLoader.load(clazz, classLoader)
                                     : ServiceLoader.load(clazz);
        if (factories.iterator().hasNext()) {
            return collect(factories);
        } else {
            factories = ServiceLoader.load(clazz, Reflections.staticClassLoader());
            if (factories.iterator().hasNext()) {
                return collect(factories);
            }
        }
        return Collections.emptyList();
    }

    private static <T> Collection<T> collect(ServiceLoader<T> factories) {
        List<T> list = new ArrayList<>();
        factories.iterator().forEachRemaining(list::add);
        return Collections.unmodifiableList(list);
    }

}
