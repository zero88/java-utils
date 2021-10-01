package io.github.zero88.repl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.mock.MockChild;
import io.github.zero88.mock.MockParent;

public class ReflectionClassTest {

    @Test
    public void test_assert_data_type_with_primitive_in_string() {
        Assert.assertTrue(ReflectionClass.assertDataType("int", int.class));
        Assert.assertTrue(ReflectionClass.assertDataType("int", Integer.class));
    }

    @Test
    public void test_assert_data_type_with_primitive() {
        Assert.assertTrue(ReflectionClass.assertDataType(int.class, int.class));
        Assert.assertTrue(ReflectionClass.assertDataType(int.class, Integer.class));
        Assert.assertTrue(ReflectionClass.assertDataType(Integer.class, int.class));
        Assert.assertTrue(ReflectionClass.assertDataType(Integer.class, Integer.class));
    }

    @Test
    public void test_assert_data_type() {
        Assert.assertFalse(ReflectionClass.assertDataType(MockParent.class, MockChild.class));
        Assert.assertTrue(ReflectionClass.assertDataType(MockChild.class, MockParent.class));
    }

    @Test
    public void test_searchClass() {
        List<Class<Object>> list = ReflectionClass.stream("io.github.zero88.mock", Object.class,
                                                                   ReflectionElement.isPublicClass())
                                                           .collect(Collectors.toList());
        Assert.assertEquals(3, list.size());
    }

}
