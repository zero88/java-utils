package io.github.zero88.repl;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.exceptions.FileException;
import io.github.zero88.exceptions.SneakyErrorCodeException;
import io.github.zero88.mock.MockReflection;

public class ReflectionMethodTest {

    //    @Test(expected = NullPointerException.class)
    //    public void test_execute_method_instance_null() {
    //        ReflectionMethod.execute(null, null, JsonObject.class, Collections.singletonList(String.class), "s");
    //    }
    //
    //    @Test(expected = NullPointerException.class)
    //    public void test_execute_method_method_null() {
    //        ReflectionMethod.execute("", null, JsonObject.class, Collections.singletonList(String.class), "s");
    //    }

    @Test(expected = NullPointerException.class)
    public void test_execute_method_output_null() throws NoSuchMethodException {
        final MockReflection mock = new MockReflection("abc");
        final Method method = mock.getClass().getDeclaredMethod("methodNoArgument");
        ReflectionMethod.execute(mock, method, null, new Arguments().put(String.class, "hello"));
    }

    @Test
    public void test_execute_method() throws NoSuchMethodException {
        final MockReflection mock = new MockReflection("abc");
        final Method method = mock.getClass().getDeclaredMethod("setName", String.class);
        ReflectionMethod.execute(mock, method, Void.class, String.class, "xxx");
        Assert.assertEquals("xxx", mock.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_execute_method_noArgument() throws NoSuchMethodException {
        final MockReflection mock = new MockReflection("abc");
        final Method method = mock.getClass().getDeclaredMethod("methodNoArgument");
        ReflectionMethod.execute(mock, method, Void.class, String.class, "xxx");
    }

    @Test(expected = FileException.class)
    public void test_execute_method_throwSneakyException() throws NoSuchMethodException {
        final MockReflection mock = new MockReflection("abc");
        final Method method = mock.getClass().getDeclaredMethod("throwSneakyException", String.class);
        try {
            ReflectionMethod.execute(mock, method, Void.class, new Arguments().put(String.class, "hey"));
        } catch (SneakyErrorCodeException e) {
            final SneakyErrorCodeException cause = (SneakyErrorCodeException) e.getCause();
            Assert.assertNotNull(cause);
            Assert.assertEquals("hey", cause.getMessage());
            Assert.assertEquals(ErrorCode.FILE_ERROR, cause.errorCode());
            throw cause;
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_execute_method_throwUnknownException() throws Throwable {
        final MockReflection mock = new MockReflection("abc");
        final Method method = mock.getClass().getDeclaredMethod("throwUnknownException", String.class);
        try {
            ReflectionMethod.execute(mock, method, Void.class, new Arguments().put(String.class, "hey"));
        } catch (SneakyErrorCodeException e) {
            Assert.assertNull(e.getMessage());
            Assert.assertEquals(ErrorCode.REFLECTION_ERROR, e.errorCode());
            Assert.assertEquals("hey", e.getCause().getMessage());
            throw e.getCause();
        }
    }

}
