package io.github.zero88.repl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Represents arguments when invoking class constructor or class method
 *
 * @see Executable
 * @see Constructor
 * @see Method
 */
public final class Arguments {

    private final List<Class<?>> classes = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();

    public Arguments put(Class<?> argClass, Object argValue) {
        this.classes.add(Objects.requireNonNull(argClass, "Argument class must be not null"));
        this.values.add(argValue);
        return this;
    }

    public Class<?>[] argClasses() {
        return classes.toArray(new Class[] {});
    }

    public Object[] argValues() {
        return values.toArray(new Object[] {});
    }

    public boolean isSame(Class<?>... argClasses) {
        if (classes.size() != argClasses.length) {
            return false;
        }
        return IntStream.range(0, classes.size())
                        .allMatch(i -> ReflectionClass.assertDataType(argClasses[i], classes.get(i)));
    }

    public boolean isSame(Arguments arguments) {
        return isSame(arguments.argClasses());
    }

    public static Arguments from(Method method) {
        final Arguments args = new Arguments();
        Arrays.stream(getMethodArgs(method)).forEach(cls -> args.put(cls, null));
        return args;
    }

    public static Arguments from(Method method, Object... argValues) {
        final Arguments args = new Arguments();
        final Class<?>[] methodArgs = getMethodArgs(method);
        if (methodArgs.length != argValues.length) {
            throw new IllegalArgumentException(
                "Given argument values does not match with the Method signature arguments");
        }
        IntStream.range(0, methodArgs.length).boxed().forEach(i -> args.put(methodArgs[i], argValues[i]));
        return args;
    }

    private static Class<?>[] getMethodArgs(Method m) {
        return Objects.requireNonNull(m, "Given method cannot be null").getParameterTypes();
    }

}
