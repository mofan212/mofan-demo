package indi.mofan;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.List;

/**
 * @author mofan
 * @link <a href="https://www.baeldung.com/java-variable-handles">Java 9 Variable Handles Demystified</a>
 * @date 2023/6/29 10:24
 */
public class VarHandleTest implements WithAssertions {
    static class VariableHandlesUnitTest {
        public int publicTestVariable = 1;
        private final int privateTestVariable = 1;
        public int variableToSet = 1;
        public int variableToCompareAndSet = 1;
        public int variableToGetAndAdd = 0;
        public byte variableToBitwiseOr = 0;
    }

    static VarHandle PUBLIC_TEST_VARIABLE;
    static VarHandle PRIVATE_TEST_VARIABLE;
    static VarHandle VARIABLE_TO_SET;
    static VarHandle VARIABLE_TO_COMPARE_AND_SET;
    static VarHandle VARIABLE_TO_GET_AND_ADD;
    static VarHandle VARIABLE_TO_BITWISE_OR;

    static {
        try {
            PUBLIC_TEST_VARIABLE = MethodHandles.lookup()
                    .in(VariableHandlesUnitTest.class)
                    .findVarHandle(VariableHandlesUnitTest.class, "publicTestVariable", int.class);
            // 对 private 的字段，应该使用 privateLookupIn()，而不是 in()，这样对 private、public、protected 都可以访问
            PRIVATE_TEST_VARIABLE = MethodHandles.privateLookupIn(VariableHandlesUnitTest.class, MethodHandles.lookup())
                    .findVarHandle(VariableHandlesUnitTest.class, "privateTestVariable", int.class);
            VARIABLE_TO_SET = MethodHandles.lookup().in(VariableHandlesUnitTest.class)
                    .findVarHandle(VariableHandlesUnitTest.class, "variableToSet", int.class);
            VARIABLE_TO_COMPARE_AND_SET = MethodHandles.lookup().in(VariableHandlesUnitTest.class)
                    .findVarHandle(VariableHandlesUnitTest.class, "variableToCompareAndSet", int.class);
            VARIABLE_TO_GET_AND_ADD = MethodHandles.lookup().in(VariableHandlesUnitTest.class)
                    .findVarHandle(VariableHandlesUnitTest.class, "variableToGetAndAdd", int.class);
            VARIABLE_TO_BITWISE_OR = MethodHandles.lookup().in(VariableHandlesUnitTest.class)
                    .findVarHandle(VariableHandlesUnitTest.class, "variableToBitwiseOr", byte.class);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @SneakyThrows
    public void testPublicVariables() {
        List<Class<?>> types = PUBLIC_TEST_VARIABLE.coordinateTypes();
        assertThat(types.size()).isEqualTo(1);
        assertThat(types.get(0)).isEqualTo(VariableHandlesUnitTest.class);
    }

    @Test
    @SneakyThrows
    public void testPrivateVariables() {
        List<Class<?>> types = PRIVATE_TEST_VARIABLE.coordinateTypes();
        assertThat(types.size()).isEqualTo(1);
        assertThat(types.get(0)).isEqualTo(VariableHandlesUnitTest.class);
    }

    @Test
    public void testArrayVariables() {
        VarHandle arraVarHandle = MethodHandles.arrayElementVarHandle(int[].class);
        List<Class<?>> types = arraVarHandle.coordinateTypes();
        assertThat(types.size()).isEqualTo(2);
        assertThat(types).containsExactly(int[].class, int.class);
    }

    @Test
    public void testAccessModes() {
        VariableHandlesUnitTest object = new VariableHandlesUnitTest();
        // read access
        // get() 方法只能接收 CoordinateTypes 作为参数
        assertThat(((int) PUBLIC_TEST_VARIABLE.get(object))).isEqualTo(1);

        // write access
        assertThat((int) VARIABLE_TO_SET.get(object)).isEqualTo(1);
        int newVal = 212;
        // set() 方法至少需要两个参数，第一个参数用于定位变量，后续参数用于设置值
        VARIABLE_TO_SET.set(object, newVal);
        assertThat((int) VARIABLE_TO_SET.get(object)).isEqualTo(newVal);
        // final 字段不允许 set
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> PRIVATE_TEST_VARIABLE.set(object, 100));

        // atomic update access
        assertThat((int) VARIABLE_TO_COMPARE_AND_SET.get(object)).isEqualTo(1);
        /*
         * compareAndSet() 除了需要 CoordinateTypes 作为参数外，还需要两个额外的参数:
         * 1. oldValue
         * 2. newValue
         * 当前值等于 oldValue 时，将值设置为 newValue，否则保持不变
         */
        VARIABLE_TO_COMPARE_AND_SET.compareAndSet(object, 2, 100);
        assertThat((int) VARIABLE_TO_COMPARE_AND_SET.get(object)).isEqualTo(1);
        VARIABLE_TO_COMPARE_AND_SET.compareAndSet(object, 1, 100);
        assertThat((int) VARIABLE_TO_COMPARE_AND_SET.get(object)).isEqualTo(100);

        // numeric atomic update access
        assertThat((int) VARIABLE_TO_GET_AND_ADD.get(object)).isEqualTo(0);
        // 类似于 j = i++，先使用再自增
        int before = (int) VARIABLE_TO_GET_AND_ADD.getAndAdd(object, 200);
        assertThat(before).isEqualTo(0);
        assertThat((int) VARIABLE_TO_GET_AND_ADD.get(object)).isEqualTo(200);

        // bitwise atomic update access
        assertThat((byte) VARIABLE_TO_BITWISE_OR.get(object)).isEqualTo((byte) 0);
        // 也类似于 j = i++，先使用再进行位运算，比如 getAndBitwiseOr() 是先使用再进行或运算
        byte byteValue = (byte) VARIABLE_TO_BITWISE_OR.getAndBitwiseOr(object, (byte) 127);
        assertThat(byteValue).isEqualTo((byte) 0);
        assertThat((byte) VARIABLE_TO_BITWISE_OR.get(object)).isEqualTo((byte) 127);
    }
}
