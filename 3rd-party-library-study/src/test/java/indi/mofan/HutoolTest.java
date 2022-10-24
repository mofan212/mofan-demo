package indi.mofan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import indi.mofan.pojo.Parent;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mofan
 * @date 2022/10/24 10:12
 */
public class HutoolTest {
    @Test
    public void testMap2Bean() {
        Map<String, Object> childMap = new HashMap<>();
        childMap.put("name", "child");
        childMap.put("age", 12);
        Map<String, Object> parentMap = new HashMap<>();
        parentMap.put("name", "father");
        parentMap.put("children", Collections.singletonList(childMap));

        Parent parent = BeanUtil.mapToBean(parentMap, Parent.class, true, CopyOptions.create());
        System.out.println(parent);
    }
}
