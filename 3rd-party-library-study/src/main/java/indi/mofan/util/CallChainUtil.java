package indi.mofan.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2022/10/11 14:55
 */
public final class CallChainUtil {

    private CallChainUtil() {
    }

    public static <T> List<List<T>> getPathByCallChainMap(Map<T, List<T>> callChainMap, T target) {
        Deque<T> main = new ArrayDeque<>();
        Deque<List<T>> secondary = new ArrayDeque<>();
        main.push(target);
        secondary.push(callChainMap.get(target));

        List<List<T>> path = new ArrayList<>();
        while (!main.isEmpty()) {
            List<T> secondaryPeek = secondary.peek();
            if (CollectionUtils.isNotEmpty(secondaryPeek)) {
                Optional<T> optional = secondaryPeek.stream().findAny();
                if (optional.isPresent()) {
                    T t = optional.get();
                    main.push(t);
                    secondaryPeek.remove(t);

                    List<T> collect = callChainMap.get(t).stream().filter(i -> !main.contains(i)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(collect)) {
                        secondary.push(collect);
                    } else {
                        List<T> list = new ArrayList<>();
                        main.descendingIterator().forEachRemaining(list::add);
                        path.add(list);
                        main.pop();
                    }
                }
            } else {
                main.pop();
                secondary.pop();
            }
        }
        return path;
    }
}
