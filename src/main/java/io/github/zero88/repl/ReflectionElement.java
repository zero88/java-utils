package io.github.zero88.repl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @see AnnotatedElement
 * @see Class
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface ReflectionElement {

    static <T extends Class> Predicate<T> hasModifiers(int... modifiers) {
        int searchMods = Arrays.stream(modifiers).reduce((left, right) -> left | right).orElse(0);
        return cls -> (cls.getModifiers() & searchMods) == searchMods;
    }

    static <T extends Class> Predicate<T> notModifiers(int... modifiers) {
        int searchMods = Arrays.stream(modifiers).reduce((left, right) -> left | right).orElse(0);
        return cls -> (cls.getModifiers() & searchMods) != searchMods;
    }

    static <T extends Class> Predicate<T> isPublicClass() {
        return (Predicate<T>) hasModifiers(Modifier.PUBLIC).and(notModifiers(Modifier.ABSTRACT))
                                                           .and(notModifiers(Modifier.INTERFACE));
    }

    @SafeVarargs
    static <T extends AnnotatedElement> Predicate<T> hasAnnotation(Class<? extends Annotation>... annotations) {
        return element -> Arrays.stream(annotations)
                                .filter(Objects::nonNull)
                                .anyMatch(a -> Objects.nonNull(element.getAnnotation(a)));
    }

}
