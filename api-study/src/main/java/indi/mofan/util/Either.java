package indi.mofan.util;

import lombok.Getter;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2022/12/16 10:13
 */
@Getter
public class Either<L, R> {
    /**
     * 异常值
     */
    private L left;

    /**
     * 正常值
     */
    private R right;

    public boolean isLeft() {
        return this.left != null;
    }

    public boolean isRight() {
        return this.right != null;
    }

    public static <L, R> Either<L, R> left(L value) {
        Either<L, R> either = new Either<>();
        either.left = value;
        return either;
    }

    public static <L, R> Either<L, R> right(R value) {
        Either<L, R> either = new Either<>();
        either.right = value;
        return either;
    }

    public <T> Either<L, T> map(Function<R, T> function) {
        if (this.isLeft()) {
            return left(this.left);
        } else {
            return right(function.apply(this.right));
        }
    }

    public static <L, R> Either<L, List<R>> sequence(List<Either<L, R>> eitherList, BinaryOperator<L> accumulator) {
        if (eitherList.stream().allMatch(Either::isRight)) {
            return right(eitherList.stream().map(Either::getRight).collect(Collectors.toList()));
        } else {
            // 取全部异常
            return left(
                    eitherList.stream()
                            .filter(Either::isLeft)
                            .map(Either::getLeft)
                            .reduce(accumulator)
                            .get()
            );
        }
    }
}
