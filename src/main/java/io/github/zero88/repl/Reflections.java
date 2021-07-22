package io.github.zero88.repl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Reflections {

    final static Logger LOGGER = LoggerFactory.getLogger(Reflections.class);

    /**
     * Gets the current thread context class loader.
     *
     * @return the context class loader, may be null
     */
    public static ClassLoader contextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Gets the class loader of this library.
     *
     * @return the static library class loader, may be null
     */
    public static ClassLoader staticClassLoader() {
        return Reflections.class.getClassLoader();
    }

    /**
     * Returns an array of class loaders initialized from the specified array.
     * <p>
     * If the input is null or empty, it defaults to both {@link #contextClassLoader()} and {@link
     * #staticClassLoader()}
     *
     * @param classLoaders Given class loaders
     * @return the array of class loaders, not null
     */
    public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
        if (classLoaders != null && classLoaders.length != 0) {
            return classLoaders;
        } else {
            ClassLoader contextClassLoader = contextClassLoader(), staticClassLoader = staticClassLoader();
            return contextClassLoader != null ? staticClassLoader != null && contextClassLoader != staticClassLoader
                                                ? new ClassLoader[] {contextClassLoader, staticClassLoader}
                                                : new ClassLoader[] {contextClassLoader} : new ClassLoader[] {};
        }
    }

    public static <T extends Member> Predicate<T> hasModifiers(int... modifiers) {
        int searchMods = Arrays.stream(modifiers).reduce((left, right) -> left | right).orElse(0);
        return member -> (member.getModifiers() & searchMods) == searchMods;
    }

    public static <T extends Member> Predicate<T> notModifiers(int... modifiers) {
        int searchMods = Arrays.stream(modifiers).reduce((left, right) -> left | right).orElse(0);
        return member -> (member.getModifiers() & searchMods) != searchMods;
    }

    /**
     * Constant means {@code public static final}
     *
     * @param <T> Type of Member
     * @return A constant predicate
     */
    public static <T extends Member> Predicate<T> predicateConstant() {
        return hasModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    }

    @SafeVarargs
    public static <T extends AnnotatedElement> Predicate<T> hasAnnotation(Class<? extends Annotation>... annotations) {
        return element -> Arrays.stream(annotations)
                                .filter(Objects::nonNull)
                                .anyMatch(a -> Objects.nonNull(element.getAnnotation(a)));
    }

    public static ReflectionScanner loadScanner() {
        return Optional.ofNullable(loadFactory(ReflectionScanner.class)).orElseGet(SimpleScanner::new);
    }

    public static <T> T loadFactory(@NonNull Class<T> clazz) {
        return Optional.ofNullable(loadFactory(clazz, contextClassLoader()))
                       .orElseGet(() -> loadFactory(clazz, staticClassLoader()));
    }

    public static <T> T loadFactory(@NonNull Class<T> clazz, @NonNull ClassLoader classLoader) {
        ServiceLoader<T> factories = ServiceLoader.load(clazz, classLoader);
        return factories.iterator().hasNext() ? factories.iterator().next() : null;
    }

}
