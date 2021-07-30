package io.github.zero88.repl;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.ServiceHelper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Reflections {

    final static Logger LOGGER = LoggerFactory.getLogger(Reflections.class);

    /**
     * Gets the current thread context class loader (TCCL).
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
            ClassLoader tccl = contextClassLoader(), cscl = staticClassLoader();
            return tccl.equals(cscl)
                   ? new ClassLoader[] {tccl}
                   : Stream.of(tccl, cscl).filter(Objects::nonNull).toArray(ClassLoader[]::new);
        }
    }

    public static ReflectionScanner loadScanner() {
        return Optional.ofNullable(ServiceHelper.loadFactory(ReflectionScanner.class))
                       .orElseGet(SimpleReplScanner::new);
    }

}
