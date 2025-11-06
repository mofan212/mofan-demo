package indi.mofan.jackson;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author mofan
 * @date 2025/11/6 16:58
 */
public class Json2MethodReturnTypeTest implements WithAssertions {

    @SneakyThrows
    private Object convert(String json, Method method) {
        ObjectMapper mapper = new ObjectMapper();
        Type genericReturnType = method.getGenericReturnType();
        JavaType javaType = mapper.getTypeFactory().constructType(genericReturnType);
        return mapper.readValue(json, javaType);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Person {
        private String name;
        private List<Person> children;

        public Person(String name) {
            this.name = name;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class PageInfo {
        private int pageNumber;
        private int pageSize;
    }

    @Data
    static class PageData<T> {
        private PageInfo pageInfo;
        private List<T> data;
    }

    private Person selectPerson() {
        return null;
    }

    private List<Person> selectPersons() {
        return null;
    }

    private PageData<Person> selectPageData() {
        return null;
    }

    @Test
    @SneakyThrows
    public void test() {
        // language=JSON
        String json = """
                {
                  "name": "Mike",
                  "children": [
                    {
                      "name": "Tom"
                    }
                  ]
                }
                """;
        Method method = this.getClass().getDeclaredMethod("selectPerson");
        Object object = convert(json, method);
        assertThat(object).isInstanceOf(Person.class)
                .asInstanceOf(InstanceOfAssertFactories.type(Person.class))
                .extracting(Person::getName, i -> i.getChildren().getFirst().getName())
                .containsExactly("Mike", "Tom");

        json = """
                [
                  {
                    "name": "Mike",
                    "children": [
                      {
                        "name": "Tom"
                      }
                    ]
                  }
                ]
                """;
        method = this.getClass().getDeclaredMethod("selectPersons");
        object = convert(json, method);
        assertThat(object).isInstanceOf(List.class)
                .asInstanceOf(InstanceOfAssertFactories.list(Person.class))
                .hasSize(1)
                .first()
                .extracting(Person::getName, i -> i.getChildren().getFirst().getName())
                .containsExactly("Mike", "Tom");

        json = """
                {
                  "pageInfo": {
                    "pageNumber": 1,
                    "pageSize": 20
                  },
                  "data": [
                    {
                      "name": "Mike",
                      "children": [
                        {
                          "name": "Tom"
                        }
                      ]
                    }
                  ]
                }
                """;
        method = this.getClass().getDeclaredMethod("selectPageData");
        object = convert(json, method);
        assertThat(object).isInstanceOf(PageData.class)
                .asInstanceOf(InstanceOfAssertFactories.type(PageData.class))
                .extracting(PageData::getPageInfo, PageData::getData)
                .containsExactly(new PageInfo(1, 20), List.of(new Person("Mike", List.of(new Person("Tom")))));
    }
}
