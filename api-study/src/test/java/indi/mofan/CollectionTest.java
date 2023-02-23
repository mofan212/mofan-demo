package indi.mofan;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2023/2/23 19:50
 */
public class CollectionTest {
    @Test
    public void testDescartes() {
        List<String> listA = Arrays.asList("A", "B", "C");
        List<String> listB = Arrays.asList("-1", "-2");

        List<List<String>> descartes = listA.stream()
                .flatMap(i -> listB.stream().map(j -> {
                    List<String> list = new ArrayList<>();
                    list.add(i);
                    list.add(j);
                    return list;
                })).collect(Collectors.toList());
        Assert.assertEquals(6, descartes.size());
        List<List<String>> expect = Arrays.asList(
                Arrays.asList("A", "-1"),
                Arrays.asList("B", "-1"),
                Arrays.asList("C", "-1"),
                Arrays.asList("A", "-2"),
                Arrays.asList("B", "-2"),
                Arrays.asList("C", "-2")
        );
        Assert.assertTrue(descartes.containsAll(expect));
    }
}
