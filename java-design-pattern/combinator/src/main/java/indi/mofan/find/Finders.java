package indi.mofan.find;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2024/3/17 22:49
 */
public class Finders {
    private Finders() {
    }

    /**
     * 包含 query 或 orQuery 的，但不包含 notQuery 的行
     */
    public static Finder advancedFinder(String query, String orQuery, String notQuery) {
        return Finder.contains(query)
                .or(Finder.contains(orQuery))
                .not(Finder.contains(notQuery));
    }

    /**
     * 包含 query 的，但不包含任一 excludeQuires 的行
     */
    public static Finder filteredFinder(String query, String... excludeQueries) {
        var finder = Finder.contains(query);
        for (String excludeQuery : excludeQueries) {
            finder = finder.not(Finder.contains(excludeQuery));
        }
        return finder;
    }

    /**
     * 包含所有 queries 的行
     */
    public static Finder specializedFinder(String... queries) {
        Finder finder = identMulti();
        for (String query : queries) {
            finder = finder.and(Finder.contains(query));
        }
        return finder;
    }

    /**
     * 包含任一 queries 的行
     */
    public static Finder expandedFinder(String... queries) {
        Finder finder = identSum();
        for (String query : queries) {
            finder = finder.or(Finder.contains(query));
        }
        return finder;
    }

    private static Finder identMulti() {
        return txt -> Stream.of(txt.split("\n")).collect(Collectors.toList());
    }

    private static Finder identSum() {
        return txt -> new ArrayList<>();
    }
}
