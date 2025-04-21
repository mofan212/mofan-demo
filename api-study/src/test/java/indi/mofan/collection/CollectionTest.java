package indi.mofan.collection;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author mofan
 * @date 2023/2/23 19:50
 */
public class CollectionTest implements WithAssertions {
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
                })).toList();

        assertThat(descartes).hasSize(6)
                .containsAll(Arrays.asList(
                        Arrays.asList("A", "-1"),
                        Arrays.asList("B", "-1"),
                        Arrays.asList("C", "-1"),
                        Arrays.asList("A", "-2"),
                        Arrays.asList("B", "-2"),
                        Arrays.asList("C", "-2")
                ));
    }

    @Test
    public void testSwap() {
        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        Collections.swap(list, 0, 1);
        assertThat(list).containsExactly("B", "A", "C");
    }
}
