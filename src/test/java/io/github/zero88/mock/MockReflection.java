package io.github.zero88.mock;

import io.github.zero88.exceptions.FileException;

public class MockReflection {

    private final String id;
    private String name;

    public MockReflection(String id) {
        this.id = id;
    }

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

    public String getId()            {return this.id;}

    public String getName()          {return this.name;}

    public void setName(String name) {this.name = name;}

}
