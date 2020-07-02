package io.github.zero88.utils;

import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.mock.MockChild;
import io.github.zero88.utils.mock.MockParent;

public class ReflectionClassTest {

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

}
