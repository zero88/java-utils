package io.github.zero88.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.exceptions.HiddenException;

public final class Functions {

    private Functions() {}

    private static final Logger logger = LoggerFactory.getLogger(Functions.class);

    public static <T> Optional<T> getIfThrow(Consumer<Throwable> consumer, @NotNull Provider<T> provider) {
        try {
            return Optional.ofNullable(provider.get());
        } catch (Throwable t) {
            consumer.accept(t);
            return Optional.empty();
        }
    }

    public static <T> T getOrThrow(@NotNull Function<Throwable, ? extends RuntimeException> override,
        @NotNull Provider<T> provider) {
        try {
            return provider.get();
        } catch (Throwable t) {
            throw override.apply(t);
        }
    }

    public static <T> Optional<T> getIfThrow(@NotNull Supplier<T> supplier) {
        return getIfThrow(supplier, throwable -> logger.trace("", throwable));
    }

    public static <T> Optional<T> getIfThrow(@NotNull Supplier<T> supplier, Consumer<Throwable> consumer) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception t) {
            consumer.accept(t);
            return Optional.empty();
        }
    }

    public static <T> T getOrThrow(@NotNull Supplier<T> supplier,
        @NotNull Supplier<? extends RuntimeException> override) {
        try {
            return supplier.get();
        } catch (Exception t) {
            throw (RuntimeException) override.get().initCause(t);
        }
    }

    public static <T> T getOrThrow(@NotNull Supplier<T> supplier,
        @NotNull Function<Throwable, ? extends RuntimeException> override) {
        try {
            return supplier.get();
        } catch (Exception e) {
            final RuntimeException t = override.apply(e);
            if (logger.isTraceEnabled()) {
                logger.trace("Root error cause", t);
            }
            throw t;
        }
    }

    public static <T> T getOrDefault(@NotNull Supplier<T> supplier, @NotNull Supplier<T> def) {
        try {
            return supplier.get();
        } catch (Exception t) {
            if (logger.isTraceEnabled()) {
                logger.trace("Fallback default", t);
            }
            return def.get();
        }
    }

    public static <T> T getOrDefault(T def, @NotNull Provider<T> provider) {
        try {
            return provider.get();
        } catch (Throwable t) {
            if (logger.isTraceEnabled()) {
                logger.trace("Fallback default", t);
            }
            return def;
        }
    }

    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<T>... predicates) {
        return Arrays.stream(predicates).reduce(Predicate::and).orElse(x -> true);
    }

    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<T>... predicates) {
        return Arrays.stream(predicates).reduce(Predicate::or).orElse(x -> false);
    }

    public static <T> Function<T, Boolean> to(Predicate<T> predicate) {
        return predicate::test;
    }

    public static Function<String, Integer> toInt() {
        return Integer::valueOf;
    }

    public static Function<String, Long> toLong() {
        return Long::parseLong;
    }

    public static Function<String, Double> toDouble() {
        return Double::parseDouble;
    }

    public static Function<String, UUID> toUUID() {
        return UUID64::uuid64ToUuid;
    }

    public static class Silencer<T> implements BiConsumer<T, HiddenException>, Supplier<T> {

        protected T object;

        @Override
        public void accept(T t, HiddenException e) {
            if (Objects.nonNull(e)) {
                logger.warn("Failed to retrieve object", e);
                return;
            }
            object = t;
        }

        @Override
        public final T get() {
            return object;
        }

    }

}
