package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * Rust 的枚举和 Java 很类似，但又比 Java 强大很多，比如可以这样声明一个求值枚举：
 * <pre>
 * enum Expr {
 *     Constant(i32),
 *     Plus(Box<Expr>, Box<Expr>),
 *     Times(Box<Expr>, Box<Expr>),
 *     Neg(Box<Expr>),
 * }
 * </pre>
 * 不用悲伤，Java 虽然没有改造原有的枚举，但通过密封 + 模式匹配实现了类似的功能！
 *
 * @author mofan
 * @date 2024/1/7 13:57
 */
public class ImitateRustEnumTest implements WithAssertions {
    private sealed interface Expr {

        record Constant(int i) implements Expr {
        }

        record Plus(Expr a, Expr b) implements Expr {
        }

        record Times(Expr a, Expr b) implements Expr {
        }

        record Neg(Expr e) implements Expr {
        }

        default int eval() {
            return switch (this) {
                case Constant(int i) -> i;
                case Plus(Expr a, Expr b) -> a.eval() + b.eval();
                case Times(Expr a, Expr b) -> a.eval() * b.eval();
                case Neg(Expr e) -> -e.eval();
            };
        }
    }

    @Test
    public void testEval() {
        var a = new Expr.Constant(10);
        var b = new Expr.Plus(a, new Expr.Constant(10));
        assertThat(b.eval()).isEqualTo(20);
    }
}
