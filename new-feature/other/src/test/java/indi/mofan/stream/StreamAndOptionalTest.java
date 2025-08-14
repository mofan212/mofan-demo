package indi.mofan.stream;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.util.Optional.of;

/**
 * @author mofan
 * @date 2023/6/11 21:29
 */
public class StreamAndOptionalTest implements WithAssertions {
    @Test
    public void testStream() {
        try {
            Stream.of(null).forEach(System.out::println);
            Assertions.fail();
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(NullPointerException.class);
        }

        Stream<Object> nullableStream = Stream.ofNullable(null);
        assertThatNoException().isThrownBy(() -> assertThat(nullableStream).hasSize(0));

        Stream<Integer> limit = Stream.iterate(0, i -> i + 1)
                .limit(10);
        assertThat(limit).hasSize(10);

        Stream<Integer> iterate = Stream.iterate(0, i -> i < 10, i -> i + 1);
        assertThat(iterate).hasSize(10);

        Stream<Integer> takeWhile = Stream.iterate(0, i -> i < 20, i -> i + 1).takeWhile(i -> i < 15);
        assertThat(takeWhile).hasSize(15);

        Stream<Integer> dropWhile = Stream.iterate(0, i -> i < 20, i -> i + 1).dropWhile(i -> i < 15);
        assertThat(dropWhile).hasSize(5);
    }

    @Test
    public void testOptional() {
        Optional.ofNullable(getEvenOrNull())
                .filter(i -> i % 2 == 0)
                .ifPresentOrElse(i -> assertThat(i).isEven(), () -> System.out.println("奇数"));

        Integer integer = Optional.ofNullable(getEvenOrNull())
                // 一定m没有这样的数
                .filter(i -> i % 2 == 1)
                .or(() -> of(2))
                // 一定都返回 2
                .get();
        assertThat(integer).isEqualTo(2);

        // 转 stream
        assertThat(of(2).stream().count()).isEqualTo(1);
    }

    private Integer getEvenOrNull() {
        int i = ThreadLocalRandom.current().nextInt(100);
        return i % 2 == 0 ? i : null;
    }
}
