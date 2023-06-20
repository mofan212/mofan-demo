package indi.mofan;


import com.fasterxml.jackson.databind.json.JsonMapper;
import indi.mofan.generator.EntityExceptionThrower;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static indi.mofan.generator.EntityExceptionThrower.Entity;

/**
 * @author mofan
 * @date 2023/6/19 17:30
 */
public class EntityExceptionThrowerTest implements WithAssertions {

    // language=JSON
    String except = """
                [
                  {
                    "0": {
                      "ceshibuerzhi": ["长度超过100"],
                      "ceshiriqishijian": ["日期时间格式不正确"],
                      "entityList": [
                        {
                          "0": {
                            "zishitibuerzhi": ["你的布尔值错了"],
                            "entityList": [
                              {
                                "1": {
                                  "zhenshuzhiduan": ["整数字段错误", "必须大于 0"]
                                }
                              }
                            ],
                            "one2OneEntity": {
                              "wenben": ["文本字段错误"]
                            }
                          }
                        },
                        {
                          "1": {
                            "zishitishuzi": ["子实体数字也不对"]
                          }
                        }
                      ]
                    }
                  }
                ]
                """;

    @Test
    @SneakyThrows
    public void test() {
        Entity master = new Entity();
        master.addErrorInfo("ceshibuerzhi", "长度超过100");
        master.addErrorInfo("ceshiriqishijian", "日期时间格式不正确");
        master.setPositionIndex(0);

        Entity firstSubEntity = new Entity();
        firstSubEntity.addErrorInfo("zishitishuzi", "子实体数字也不对");
        firstSubEntity.setPositionIndex(1);

        Entity secondSubEntity = new Entity();
        secondSubEntity.addErrorInfo("zishitibuerzhi", "你的布尔值错了");
        secondSubEntity.setPositionIndex(0);

        master.setEntityList(List.of(firstSubEntity, secondSubEntity));

        Entity firstGrandEntity = new Entity();
        firstGrandEntity.addErrorInfo("zhenshuzhiduan", "整数字段错误");
        firstGrandEntity.addErrorInfo("zhenshuzhiduan", "必须大于 0");
        firstGrandEntity.setPositionIndex(1);
        secondSubEntity.setEntityList(List.of(firstGrandEntity));

        Entity one2OneEntity = new Entity();
        one2OneEntity.addErrorInfo("wenben", "文本字段错误");
        secondSubEntity.setOne2OneEntity(one2OneEntity);

        List<Map<Integer, Object>> throwing = EntityExceptionThrower.throwing(List.of(master));
        String json = JsonMapper.builder().build().writerWithDefaultPrettyPrinter().writeValueAsString(throwing);
        System.out.println(json);
    }
}
