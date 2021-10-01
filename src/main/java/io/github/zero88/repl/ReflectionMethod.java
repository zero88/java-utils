package io.github.zero88.repl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.exceptions.ReflectionException;

import lombok.NonNull;

@SuppressWarnings("unchecked")
public final class ReflectionMethod implements ReflectionMember {

    /**
     * Execute static method
     *
     * @param declareCls a declare class
     * @param outputCls  an output class
     * @param methodName a method name
     * @param args       a object arguments
     * @param <T>        Type of output
     * @return method result
     */
    public static <T> T executeStatic(@NonNull Class<?> declareCls, @NonNull Class<T> outputCls,
                                      @NonNull String methodName, Object... args) {
        final Predicate<Method> predicate = m -> m.getReturnType().equals(outputCls) && m.getName().equals(methodName);
        return (T) find(predicate, declareCls).findFirst()
                                              .map(method -> doExecute(null, method, Arguments.from(method, args)))
                                              .orElse(null);
    }

    /**
     * Execute method with no arguments.
     *
     * @param instance an object instance
     * @param method   a method to execute
     * @return the method result
     */
    public static Object execute(@NonNull Object instance, @NonNull Method method) {
        return execute(instance, method, new Arguments());
    }

    /**
     * Execute method with arguments.
     *
     * @param instance an object instance
     * @param method   a method to execute
     * @return the method result
     */
    public static Object execute(@NonNull Object instance, @NonNull Method method, @NonNull Arguments args) {
        Arguments arguments = Arguments.from(method);
        if (!arguments.isSame(args)) {
            throw new ReflectionException("Given arguments does not match the Method signature arguments");
        }
        return doExecute(instance, method, args);
    }

    public static <O> O execute(@NonNull Object instance, @NonNull Method method, Object... args) {
        return (O) doExecute(instance, method, Arguments.from(method, args));
    }

    /**
     * Execute method from object instance by only one param.
     *
     * @param <I>        Type of Input parameter
     * @param <O>        Type of Return result
     * @param instance   Object instance
     * @param method     Method to execute
     * @param outputType Output type of {@code method}
     * @param argClass   Param type of {@code method}
     * @param argValue   Argument to pass into {@code method}
     * @return the method result
     * @throws ReflectionException if any error when invoke method
     */
    public static <I, O> O execute(@NonNull Object instance, @NonNull Method method, @NonNull Class<O> outputType,
                                   @NonNull Class<I> argClass, I argValue) {
        return execute(instance, method, outputType, new Arguments().put(argClass, argValue));
    }

    public static <O> O execute(@NonNull Object instance, @NonNull Method method, @NonNull Class<O> outputType,
                                @NonNull Arguments arguments) {
        if (!validateMethod(method, outputType, arguments)) {
            throw new IllegalArgumentException("Given method does not match with given output type and input type");
        }
        return (O) doExecute(instance, method, arguments);
    }

    /**
     * Find declared methods in given {@code class} that matches with filter
     *
     * @param clazz     Given {@code class} to find methods
     * @param predicate Given predicate
     * @return List of matching {@code methods}
     */
    public static List<Method> find(@NotNull Class<?> clazz, Predicate<Method> predicate) {
        return find(predicate, clazz).collect(Collectors.toList());
    }

    public static Stream<Method> find(Predicate<Method> predicate, @NotNull Class<?> clazz) {
        return Reflections.loadScanner().methodStream(clazz, predicate);
    }

    /**
     * Check given {@code Method} is matched with given {@code output} class and {@code inputs} class
     *
     * @param method     Given method
     * @param outputType Given output type
     * @param argClasses Given argument classes
     * @return {@code true} if matched, otherwise {@code false}
     */
    public static boolean validateMethod(Method method, Class<?> outputType, Class<?>... argClasses) {
        return ReflectionClass.assertDataType(outputType, method.getReturnType()) &&
               Arguments.from(method).isSame(argClasses);
    }

    /**
     * Check given {@code Method} is matched with given {@code output} class and {@code inputs} class
     *
     * @param method     Given method
     * @param outputType Given output type
     * @param argClasses Given argument classes
     * @return {@code true} if matched, otherwise {@code false}
     */
    public static boolean validateMethod(@NonNull Method method, @NonNull Class<?> outputType,
                                         @NonNull Collection<Class<?>> argClasses) {
        return validateMethod(method, outputType, argClasses.toArray(new Class[] {}));
    }

    /**
     * Check given {@code Method} is matched with given {@code output} class and {@code inputs} class
     *
     * @param method     Given method
     * @param outputType Given output type
     * @param arguments  Given arguments
     * @return {@code true} if matched, otherwise {@code false}
     */
    public static boolean validateMethod(@NonNull Method method, @NonNull Class<?> outputType,
                                         @NonNull Arguments arguments) {
        return validateMethod(method, outputType, arguments.argClasses());
    }

    private static Object doExecute(Object instance, Method method, Arguments args) {
        try {
            method.setAccessible(true);
            return method.invoke(instance, args.argValues());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw ReflectionMember.handleError(method, e);
        }
    }

}
