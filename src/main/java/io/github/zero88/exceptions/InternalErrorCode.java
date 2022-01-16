package io.github.zero88.exceptions;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

final class InternalErrorCode implements ErrorCode {

    private final String code;

    public InternalErrorCode(String code) {
        this.code = code;
    }

    public int hashCode() {
        return this.code().hashCode();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof String) {
            return Objects.equals(this.code(), o);
        }
        if (!(o instanceof ErrorCode)) {
            return false;
        }
        return Objects.equals(this.code(), ((ErrorCode) o).code());
    }

    public @NotNull String code() {return this.code;}

}
