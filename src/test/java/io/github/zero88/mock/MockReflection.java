package io.github.zero88.mock;

import io.github.zero88.exceptions.FileException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class MockReflection {

    private final String id;
    @Setter
    private String name;

    public int methodNoArgument() {
        return 1;
    }

    public int methodWithArgument(String abc) {
        return 2;
    }

    public void throwSneakyException(String hey) {
        throw new FileException(hey);
    }

    public void throwUnknownException(String hey) {
        throw new RuntimeException(hey);
    }

}
