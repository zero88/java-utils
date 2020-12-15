package io.github.zero88.exceptions;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("ALL")
public class ErrorCodeTest {

    @Test
    public void equalsByString() {
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, "UNKNOWN_ERROR");
    }

    @Test
    public void equalsByObject() {
        Assert.assertEquals(ErrorCode.UNKNOWN_ERROR, new InternalErrorCode("UNKNOWN_ERROR"));
    }
}