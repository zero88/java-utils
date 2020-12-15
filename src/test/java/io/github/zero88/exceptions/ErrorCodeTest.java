package io.github.zero88.exceptions;

import org.junit.Assert;
import org.junit.Test;

public class ErrorCodeTest {

    @Test
    public void equalsByString() {
        Object actual = "UNKNOWN_ERROR";
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, actual);
    }

    @Test
    public void equalsByObject() {
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, new InternalErrorCode("UNKNOWN_ERROR"));
    }
}