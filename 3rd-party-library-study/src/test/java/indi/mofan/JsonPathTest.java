package indi.mofan;

import cn.hutool.core.io.FileUtil;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author mofan
 * @date 2023/2/28 15:01
 */
public class JsonPathTest {
    private static final String JSON = FileUtil.readString("json-path.json", Charset.defaultCharset());

    private static final Object DOCUMENT = Configuration.defaultConfiguration().jsonProvider().parse(JSON);

    @Test
    public void testPathExample() {
        // store 下所有 book 列表的 author 信息
        List<String> authors = JsonPath.read(DOCUMENT, "$.store.book[*].author");
        Assertions.assertEquals(4, authors.size());

        // JSON 数据中所有的 author 值
        authors = JsonPath.read(DOCUMENT, "$..author");
        Assertions.assertEquals(4, authors.size());

        // store 下的所有信息（返回的是一个列表，而不是 Map）
        List<Object> all = JsonPath.read(DOCUMENT, "$.store.*");
        Assertions.assertEquals(2, all.size());
        Assertions.assertTrue(all.get(0) instanceof List);
        List<?> allBooks = (List<?>) all.get(0);
        Assertions.assertEquals(4, allBooks.size());

        // store 下所有 price
        List<Double> prices = JsonPath.read(DOCUMENT, "$.store..price");
        // 还有 bicycle 下的 price
        Assertions.assertEquals(5, prices.size());

        // 获取 book 数组的第三个值
        List<Map<String, Object>> thirdBooks = JsonPath.read(DOCUMENT, "$..book[2]");
        Assertions.assertEquals(1, thirdBooks.size());
        Map<String, Object> thirdBook = thirdBooks.get(0);
        Assertions.assertEquals(5, thirdBook.size());
        Assertions.assertEquals("0-553-21311-3", thirdBook.get("isbn"));

        // 获取 book 数据的倒数第二本书
        List<Map<String, Object>> books = JsonPath.read(DOCUMENT, "$..book[-2]");
        Assertions.assertEquals(1, books.size());
        Map<String, Object> penultimateBook = books.get(0);
        Assertions.assertEquals(5, penultimateBook.size());
        Assertions.assertEquals("0-553-21311-3", thirdBook.get("isbn"));

        // 前两本书
        books = JsonPath.read(DOCUMENT, "$..book[0, 1]");
        Assertions.assertEquals(2, books.size());
        Assertions.assertEquals("reference", books.get(0).get("category"));
        Assertions.assertEquals("fiction", books.get(1).get("category"));

        // 前两本书
        books = JsonPath.read(DOCUMENT, "$..book[:-2]");
        Assertions.assertEquals("reference", books.get(0).get("category"));
        Assertions.assertEquals("fiction", books.get(1).get("category"));

        // 索引 [0, 2) 的树
        books = JsonPath.read(DOCUMENT, "$..book[:2]");
        Assertions.assertEquals(2, books.size());
        Assertions.assertEquals("reference", books.get(0).get("category"));
        Assertions.assertEquals("fiction", books.get(1).get("category"));

        // 索引 [1, 2) 的树
        books = JsonPath.read(DOCUMENT, "$..book[1:2]");
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals("fiction", books.get(0).get("category"));

        // 最后两本书
        books = JsonPath.read(DOCUMENT, "$..book[-2:]");
        Assertions.assertEquals(2, books.size());
        boolean allMatch = books.stream().map(i -> String.valueOf(i.get("category"))).allMatch("fiction"::equals);
        Assertions.assertTrue(allMatch);

        // 获取从第三本书开始（包括第三本）的所有数据
        books = JsonPath.read(DOCUMENT, "$..book[2:]");
        Assertions.assertEquals(2, books.size());

        // 书籍信息中包含 isbn 的书
        books = JsonPath.read(DOCUMENT, "$..book[?(@.isbn)]");
        Assertions.assertEquals(2, books.size());

        // 书籍信息中 price < 10 的所有书
        books = JsonPath.read(DOCUMENT, "$..book[?(@.price < 10)]");
        Assertions.assertEquals(2, books.size());

        // 使用原始 JSON 数据中的值进行过滤
        books = JsonPath.read(DOCUMENT, "$..book[?(@.price < $['expensive'])]");
        Assertions.assertEquals(2, books.size());

        // 获取书籍信息中的 author 以 REES 结尾的所有书（REES 不区分大小写）
        books = JsonPath.read(DOCUMENT, "$..book[?(@.author =~ /.*REES/i)]");
        Assertions.assertEquals(1, books.size());

        // JSON 中所有值
        List<Object> value = JsonPath.read(DOCUMENT, "$..*");
        Assertions.assertTrue(value.contains("Herman Melville"));
        Assertions.assertTrue(value.stream().anyMatch(i -> Map.class.isAssignableFrom(i.getClass())));

        // book 的数量
        int number = JsonPath.read(DOCUMENT, "$..book.length()");
        Assertions.assertEquals(4, number);
    }
}
