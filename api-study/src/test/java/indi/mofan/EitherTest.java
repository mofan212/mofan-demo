package indi.mofan;

import indi.mofan.pojo.User;
import indi.mofan.util.Either;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2022/12/16 10:32
 */
public class EitherTest {
    @Test
    public void testReturnOneError() {
        // 不使用 Either，Stream 中不允许抛出异常
        try {
            List<User> userList = Stream.iterate(1, i -> i + 1).limit(100)
                    .map(i -> {
                        try {
                            return readLineAndThrow(i);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());

            userList.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testUseEither() {
        // 使用 Either 收集所有错误信息
        List<Either<String, User>> eitherList = Stream.iterate(1, i -> i + 1).limit(100)
                .map(this::readLine)
                .collect(Collectors.toList());
        Either<String, List<User>> either = Either.sequence(eitherList, (s1, s2) -> s1 + "\n" + s2);
        if (either.isLeft()) {
            System.out.println(either.getLeft());
        } else {
            either.getRight().forEach(System.out::println);
        }
    }

    /**
     * 模拟读取 Excel 的一行时，抛出异常
     */
    private User readLineAndThrow(int i) throws Exception {
        if (new Random().nextInt(100) <= 90) {
            return new User("默烦", 20);
        } else {
            throw new Exception("第" + i + "行数据错误");
        }
    }

    private Either<String, User> readLine(int i) {
        if (new Random().nextInt(100) <= 90) {
            return Either.right(new User("mofan", 20));
        } else {
            return Either.left("第" + i + "行数据错误");
        }
    }
}
