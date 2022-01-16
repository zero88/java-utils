package io.github.zero88.utils;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * HTTP scheme.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7230#section-8.2">URI Scheme</a>
 * @since 1.0.0
 */
public enum HttpScheme {

    HTTP("http"), HTTPS("https");

    public static final String SCHEME_REGEX = "(https?://)";
    private final String scheme;

    HttpScheme(String scheme) {this.scheme = scheme;}

    @JsonCreator
    public static HttpScheme parse(String name) {
        return Arrays.stream(HttpScheme.values()).filter(s -> s.scheme.equalsIgnoreCase(name)).findFirst().orElse(HTTP);
    }

    @Override
    public String toString() {
        return this.scheme;
    }

    public String getScheme() {return this.scheme;}
}
