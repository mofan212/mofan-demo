package indi.mofan.find;

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

    /**
     * <p>相当于接口的默认实现。</p>
     * <p>
     * 在实现中将传入字符串 txt 按换行符拆分，返回包含指定 word（忽略大小写）的所有行。
     * </p>
     */
    static Finder contains(String word) {
        return txt -> Stream.of(txt.split("\n"))
                .filter(i -> i.toLowerCase().contains(word.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 先用当前 finder 查找出所需要的行，然后再移除使用
     * notFinder 查找出的所有行。
     */
    default Finder not(Finder notFinder) {
        return txt -> {
            List<String> res = this.find(txt);
            res.removeAll(notFinder.find(txt));
            return res;
        };
    }

    /**
     * 先用当前 finder 查找出所需要的行，然后再追加使用
     * orFinder 查找出的所有行。
     */
    default Finder or(Finder orFinder) {
        return txt -> {
            List<String> res = this.find(txt);
            res.addAll(orFinder.find(txt));
            return res;
        };
    }

    /**
     * 先用当前的 finder 查找出所需要的行，再以这些行作为原始
     * 文本，使用 andFinder 再查找出满足要求的行，最后把这些
     * 行扁平化处理得到最终的结果。
     */
    default Finder and(Finder andFinder) {
        return txt -> this.find(txt).stream()
                .flatMap(i -> andFinder.find(i).stream())
                .collect(Collectors.toList());
    }
}
