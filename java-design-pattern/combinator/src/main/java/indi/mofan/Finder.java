package indi.mofan;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2024/3/17 22:42
 */
public interface Finder {
    /**
     * 在文本中找到每行
     */
    List<String> find(String text);

    static Finder contains(String word) {
        return txt -> Stream.of(txt.split("\n"))
                .filter(i -> i.toLowerCase().contains(word.toLowerCase()))
                .collect(Collectors.toList());
    }

    default Finder not(Finder notFinder) {
        return txt -> {
            List<String> res = this.find(txt);
            res.removeAll(notFinder.find(txt));
            return res;
        };
    }

    default Finder or(Finder orFinder) {
        return txt -> {
            List<String> res = this.find(txt);
            res.addAll(orFinder.find(txt));
            return res;
        };
    }

    default Finder and(Finder andFinder) {
        return txt -> this.find(txt).stream()
                .flatMap(i -> andFinder.find(i).stream())
                .collect(Collectors.toList());
    }
}
