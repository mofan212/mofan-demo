package indi.mofan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import indi.mofan.constant.JsonConstant;
import indi.mofan.pojo.Child;
import indi.mofan.pojo.Parent;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author mofan
 * @date 2022/10/24 10:12
 */
public class HutoolTest implements WithAssertions {
    @Test
    public void testMap2Bean() {
        Map<String, Object> childMap = new HashMap<>();
        childMap.put("name", "child");
        childMap.put("age", 12);
        Map<String, Object> parentMap = new HashMap<>();
        parentMap.put("name", "father");
        parentMap.put("children", Collections.singletonList(childMap));

        Parent parent = BeanUtil.toBean(parentMap, Parent.class);
        assertThat(parent).isNotNull()
                .extracting(Parent::getName, Parent::getChildren)
                .contains("father", Index.atIndex(0))
                .element(1, InstanceOfAssertFactories.list(Child.class))
                .hasSize(1)
                .first()
                .extracting(Child::getName, Child::getAge)
                .hasSize(2)
                .containsExactly("child", 12);
    }

    @Test
    public void testJSONUtil() {
        JSONObject jsonObject = new JSONObject(JsonConstant.JSON);
        Object value = JSONUtil.getByPath(jsonObject, "store.book[0].author");
        assertThat(value).isEqualTo("Nigel Rees");
    }

    @Test
    public void testLambdaUtil() {
        Function<User, String> function = User::getName;
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> LambdaUtil.getFieldName(i -> function));

        String fieldName = LambdaUtil.getFieldName(User::getName);
        assertThat(fieldName).isEqualTo("name");

        Func1<User, String> func1 = User::getName;
        fieldName = LambdaUtil.getFieldName(func1);
        assertThat(fieldName).isEqualTo("name");
    }

    @Getter
    @Setter
    private static class User {
        private String name;
    }
}
