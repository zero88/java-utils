package io.github.zero88.utils;

@FunctionalInterface
public interface TripleFunction<T1, T2, T3, R> {

    R apply(T1 t1, T2 t2, T3 t3);

}
