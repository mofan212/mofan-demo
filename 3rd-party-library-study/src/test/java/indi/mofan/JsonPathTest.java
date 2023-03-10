package indi.mofan;

import cn.hutool.core.io.FileUtil;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.cache.Cache;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import com.jayway.jsonpath.spi.cache.LRUCache;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mofan
 * @date 2023/2/28 15:01
 * @link https://github.com/json-path/JsonPath
 */
public class JsonPathTest {

    //language=JSON
    static final String FUNC_JSON = "{\n" +
            "  \"text\": [\"A\", \"B\"],\n" +
            "  \"nums\": [1, 2]\n" +
            "}";

    @Test
    public void testConcatFunc() {
        DocumentContext context = JsonPath.parse(FUNC_JSON);

        String value = context.read("$.text.concat()");
        Assertions.assertEquals("AB", value);

        value = context.read("$.concat($.text[0], $.nums[0])");
        Assertions.assertEquals("A1", value);

        value = context.read("$.text.concat(\"-\", \"CD\")");
        Assertions.assertEquals("AB-CD", value);
    }

    @Test
    public void testAppendFunc() {
        DocumentContext context = JsonPath.parse(FUNC_JSON);

        // 1 + 2 + 3 + 4
        double sum = context.read("$.nums.append(3, 4).sum()");
        Assertions.assertEquals(10.0, sum);

        // 1 + 2 + 3 + 4 + 5 + 6
        sum = context.read("$.nums.append(\"3\", \"4\").sum(5, 6)");
        Assertions.assertEquals(21, sum);
    }

    @Test
    public void testIndexFunc() {
        DocumentContext context = JsonPath.parse(FUNC_JSON);

        Integer value = context.read("$.nums[0]");
        Assertions.assertEquals(1, value);

        value = context.read("$.nums[-1]");
        Assertions.assertEquals(2, value);

        Assertions.assertThrowsExactly(PathNotFoundException.class, () -> {
            context.read("$.nums[2]");
        });
    }

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

    @Test
    public void testReadADocumentWithFluentApi() {
        // 不使用 fluent api
        DocumentContext ctx = JsonPath.parse(JSON);
        List<String> authorsOfBooksWithISBN = ctx.read("$.store.book[?(@.isbn)].author");
        Assertions.assertEquals(2, authorsOfBooksWithISBN.size());

        // 使用 fluent api
        List<Map<String, Object>> expensiveBooks = JsonPath.using(Configuration.defaultConfiguration())
                .parse(JSON)
                .read("$.store.book[?(@.price > 10)]");
        Assertions.assertEquals(2, expensiveBooks.size());
    }

    @Test
    public void testWhatIsReturnedWhen() {
        DocumentContext context = JsonPath.parse(JSON);

        Assertions.assertThrowsExactly(ClassCastException.class, () -> {
           List<String> list = context.read("$.store.book[0].author");
        });

        String author = context.read("$.store.book[0].author");
        Assertions.assertEquals("Nigel Rees", author);
    }

    @Test
    public void testMappingLongToDate() {
        String json = "{\"date_as_long\" : 1678377600000}";

        Date date = JsonPath.parse(json).read("$['date_as_long']", Date.class);
        Assertions.assertEquals("2023-03-10", new SimpleDateFormat("yyyy-MM-dd").format(date));
    }

    @Getter
    @Setter
    static class Book {
        private String category;
        private String author;
        private String title;
        private Double price;
    }

    @Test
    public void testMappingResultToPojoOrGeneric() {
        Configuration conf = Configuration.builder()
                .jsonProvider(new JacksonJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .options(EnumSet.noneOf(Option.class))
                .build();

        DocumentContext context = JsonPath.using(conf).parse(JSON);
        Book book = context.read("$.store.book[0]", Book.class);
        Assertions.assertEquals("reference", book.getCategory());

        TypeRef<List<String>> typeRef = new TypeRef<List<String>>() {};
        List<String> titles = context.read("$.store.book[*].title", typeRef);
        Assertions.assertEquals(4, titles.size());
    }

    @Test
    public void testPredicates() {
        // inlines predicates
        List<Map<String, Object>> books = JsonPath.read(JSON, "$..book[?(@.price < 10 && @.category == 'fiction')]");
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals("Herman Melville", books.get(0).get("author"));
        books = JsonPath.read(JSON, "$..book[?(@.category == 'reference' || @.price > 10)]");
        Assertions.assertEquals(3, books.size());
        books = JsonPath.read(JSON, "$..book[?(!(@.price < 10 && @.category == 'fiction'))]");
        Assertions.assertEquals(3, books.size());

        // filter predicates
        Filter filter = Filter.filter(
                Criteria.where("category").is("fiction").and("price").lte(10D)
        );
        books = JsonPath.read(JSON, "$..book[?]", filter);
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals("0-553-21311-3", books.get(0).get("isbn"));
        Filter fooOrBar = Filter.filter(Criteria.where("foo").exists(true)).or(Criteria.where("bar").exists(true));
        books = JsonPath.read(JSON, "$..book[?]", fooOrBar);
        Assertions.assertTrue(books.isEmpty());
        Filter fooAndBar = Filter.filter(Criteria.where("foo").exists(true)).and(Criteria.where("bar").exists(true));
        books = JsonPath.read(JSON, "$..book[?]", fooAndBar);
        Assertions.assertTrue(books.isEmpty());

        // roll your own
        Predicate predicate = ctx -> ctx.item(Map.class).containsKey("isbn");
        books = JsonPath.read(JSON, "$..book[?]", predicate);
        Assertions.assertEquals(2, books.size());
    }

    @Test
    public void testReturnPath() {
        Configuration conf = Configuration.builder()
                .options(Option.AS_PATH_LIST)
                .build();
        List<String> pathList = JsonPath.using(conf)
                .parse(JSON)
                .read("$..author");
        Assertions.assertIterableEquals(pathList, Arrays.asList(
                "$['store']['book'][0]['author']",
                "$['store']['book'][1]['author']",
                "$['store']['book'][2]['author']",
                "$['store']['book'][3]['author']"
        ));
    }

    @Test
    public void testSetAValue() {
        String newJson = JsonPath.parse(JSON)
                .set("$.store.book[0].author", "Paul")
                .jsonString();

        String firstBookAuthor = JsonPath.parse(newJson).read("$.store.book[0].author", String.class);
        Assertions.assertEquals("Paul", firstBookAuthor);
    }

    //language=JSON
    static final String TEST_JSON = "[\n" +
            "  {\n" +
            "    \"name\" : \"john\",\n" +
            "    \"gender\" : \"male\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\" : \"ben\"\n" +
            "  }\n" +
            "]";

    @Test
    public void testDefaultPathLeafToNull() {
        Configuration conf = Configuration.defaultConfiguration();
        String gender0 = JsonPath.using(conf).parse(TEST_JSON).read("$.[0].gender");
        Assertions.assertEquals("male", gender0);
        Assertions.assertThrowsExactly(PathNotFoundException.class, () -> {
            JsonPath.using(conf).parse(TEST_JSON).read("$.[1].gender");
        });

        // add DEFAULT_PATH_LEAF_TO_NULL option
        Configuration conf2 = conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        gender0 = JsonPath.using(conf2).parse(TEST_JSON).read("$.[0].gender");
        Assertions.assertEquals("male", gender0);
        Assertions.assertNull(JsonPath.using(conf2).parse(TEST_JSON).read("$.[1].gender"));
    }

    @Test
    public void testAlwaysReturnList() {
        Configuration conf = Configuration.defaultConfiguration();
        Object gender0 = JsonPath.using(conf).parse(TEST_JSON).read("$.[0].gender");
        Assertions.assertEquals("male", gender0);
        Assertions.assertThrowsExactly(ClassCastException.class, () -> {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) gender0;
        });

        Configuration conf2 = conf.addOptions(Option.ALWAYS_RETURN_LIST);
        Object gender2 = JsonPath.using(conf2).parse(TEST_JSON).read("$.[0].gender");
        Assertions.assertTrue(gender2 instanceof List);
    }

    @Test
    public void testSuppressExceptions() {
        Configuration conf = Configuration.defaultConfiguration();
        Assertions.assertThrowsExactly(PathNotFoundException.class, () -> {
            JsonPath.using(conf).parse(TEST_JSON).read("$.[1].gender");
        });

        Configuration conf2 = conf.addOptions(Option.SUPPRESS_EXCEPTIONS);
        Object gender1 = JsonPath.using(conf2).parse(TEST_JSON).read("$.[1].gender");
        Assertions.assertNull(gender1);

        Configuration conf3 = conf2.addOptions(Option.ALWAYS_RETURN_LIST);
        gender1 = JsonPath.using(conf3).parse(TEST_JSON).read("$.[1].gender");
        Assertions.assertTrue(gender1 instanceof List);
        Assertions.assertTrue(((List<?>) gender1).isEmpty());
    }

    @Test
    public void testRequireProperties() {
        Configuration conf = Configuration.defaultConfiguration();
        List<String> genders = JsonPath.using(conf).parse(TEST_JSON).read("$.[*].gender");
        Assertions.assertEquals(1, genders.size());
        Assertions.assertEquals("male", genders.get(0));

        Configuration conf2 = conf.addOptions(Option.REQUIRE_PROPERTIES);
        Assertions.assertThrowsExactly(PathNotFoundException.class, () -> {
            JsonPath.using(conf2).parse(TEST_JSON).read("$.[*].gender");
        });
    }

    @Test
    @SneakyThrows
    public void testCacheSpi() {
        List<String> names = JsonPath.read(TEST_JSON, "$.[*].name");
        Assertions.assertEquals(2, names.size());
        Cache cache = CacheProvider.getCache();
        Assertions.assertTrue(cache instanceof LRUCache);
        LRUCache lruCache = (LRUCache) cache;
        Assertions.assertTrue(lruCache.size() >= 1);
        Field mapField = lruCache.getClass().getDeclaredField("map");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, ?> concurrentHashMap = (ConcurrentHashMap<String, ?>) mapField.get(lruCache);
        Assertions.assertTrue(concurrentHashMap.containsKey("$.[*].name"));
    }
}
