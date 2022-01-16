package io.github.zero88.utils;

@FunctionalInterface
public interface Provider<T> {

    T get() throws Throwable;

}
