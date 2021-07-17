package io.github.zero88.repl;

import org.junit.Assert;
import org.junit.Test;

public class ArgumentsTest {

    @Test
    public void test_primitive_arg() {
        Assert.assertEquals(Integer.valueOf(1), Arguments.castArgValue(Integer.class, 1));
        Assert.assertEquals(1, (int) Arguments.castArgValue(Integer.class, 1));
    }

}