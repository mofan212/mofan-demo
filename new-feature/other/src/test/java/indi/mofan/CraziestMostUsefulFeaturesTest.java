package indi.mofan;


import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.function.DoubleBinaryOperator;


/**
 * @author mofan
 * @date 2025/8/13 11:29
 * @link <a href="https://www.reddit.com/r/java/comments/1mgcawt/teach_me_the_craziest_most_useful_java_features/">Teach Me the Craziest, Most Useful Java Features — NOT the Basic Stuff</a>
 */
public class CraziestMostUsefulFeaturesTest implements WithAssertions {

    enum Operation {
        ADD(Double::sum),
        SUB((a, b) -> a - b),
        MUL((a, b) -> a * b),
        DIV((a, b) -> a / b);

        private final DoubleBinaryOperator op;

        Operation(DoubleBinaryOperator op) {
            this.op = op;
        }

        double apply(double a, double b) {
            return op.applyAsDouble(a, b);
        }
    }

    @Test
    public void testOperation() {
        assertThat(Operation.ADD.apply(1, 2)).isEqualTo(3.0);
        assertThat(Operation.SUB.apply(2, 1)).isEqualTo(1.0);
        assertThat(Operation.MUL.apply(1, 2)).isEqualTo(2.0);
        assertThat(Operation.DIV.apply(2, 1)).isEqualTo(2.0);
    }

    static class Reified {
        @SafeVarargs
        @SuppressWarnings("unchecked")
        private static <T> T newInstance(T... stub) {
            Class<T> reifiedType = (Class<T>) stub.getClass().getComponentType();
            try {
                return reifiedType.getDeclaredConstructor().newInstance();
            } catch (Exception exception) {
                throw new RuntimeException("Oops", exception);
            }
        }
    }

    @Test
    public void testNewInstance() {
        Date date = Reified.newInstance();
        assertThat(date).isNotNull();
    }

    @Test
    public void testTakeOutDecimal() {
        double a = 1.1;
        assertThat((int) a).isEqualTo(1);
    }
}
