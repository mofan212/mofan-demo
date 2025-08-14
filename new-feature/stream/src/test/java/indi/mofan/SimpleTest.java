package indi.mofan;

import indi.mofan.domain.Trader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author mofan
 * @date 2021/12/16 17:38
 */
public class SimpleTest {

    @Test
    public void testMatch() {
        Trader trader1 = new Trader("name1", "city1");
        Trader trader2 = new Trader("name2", "city2");
        Trader trader3 = new Trader("name3", "city3");
        Trader trader4 = new Trader("name4", "city4");


        Trader target = new Trader("name4", "city4");

        Stream.of(trader1, trader2, trader3, trader4)
                .filter(i -> Stream.of(target).noneMatch(j -> Objects.equals(j.getCity(), i.getCity())))
                .collect(Collectors.toList()).forEach(System.out::println);

    }

    @Test
    public void testRange() {
        List<String> list = Arrays.asList("One", "Two", "Three");
        IntStream.range(0, list.size()).forEach(i -> {
            System.out.print(list.get(i) + i);
        });
    }

    public static Double sum(List<? extends Number> list) {
        double sum = 0;
        for (Number number : list) {
            sum += number.doubleValue();
        }
        return sum;
    }

    @Test
    public void testSpliterator() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        Spliterator<Integer> spliterator = list.spliterator();
        if (spliterator.tryAdvance(i -> System.out.println(i + 100))) {
            StreamSupport.stream(spliterator, false).forEach(System.out::println);
        }
    }
    
    @Test
    public void testGetDuplicate() {
        Stream.of("1", "2", "2", "3", "3", "4", "5", "6", "6", "6")
                .collect(Collectors.toMap(Function.identity(), i -> 1, Integer::sum))
                .entrySet()
                .stream()
                .filter(i -> i.getValue() > 1)
                .map(Map.Entry::getKey)
                .forEach(System.out::print);
    }
}
