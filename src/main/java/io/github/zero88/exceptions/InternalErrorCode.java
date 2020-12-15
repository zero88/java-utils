package io.github.zero88.exceptions;

import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
final class InternalErrorCode implements ErrorCode {

    private final String code;

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

}
