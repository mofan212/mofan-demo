package indi.mofan;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.mofan.generator.ErrorStructureGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mofan
 * @date 2023/6/15 16:09
 */
public class ErrorStructureGeneratorTest implements WithAssertions {

    //language=JSON
    String jsonStructure = """
            {
              "ceshibuerzhi": "长度超过100",
              "ceshiriqishijian": "日期时间格式不正确",
              "ceshizishitiDtoList": [
                {
                  "0": {
                    "zishitibuerzhi": "你的布尔值错了",
                    "yiduiduo": [
                      {
                        "1": {
                          "zhenshuzhiduan": "整数字段错误"
                        }
                      }
                    ],
                    "yiduiyi": {
                      "wenben": "文本字段错误"
                    }
                  }
                },
                {
                  "1": {
                    "zishitishuzi": "子实体数字也不对"
                  }
                }
              ]
            }
            """;

    @Test
    @SneakyThrows
    public void testGenerate() {
        List<GrandEntity> grandEntityList_1 = List.of(
                new GrandEntity(11, "grandEntity-1-1"),
                new GrandEntity(12, "grandEntity-1-2"),
                new GrandEntity(13, "grandEntity-1-3")
        );

        List<GrandEntity> grandEntityList_2 = List.of(
                new GrandEntity(21, "grandEntity-2-1"),
                new GrandEntity(22, "grandEntity-2-2"),
                new GrandEntity(23, "grandEntity-2-3")
        );

        List<GrandEntity> grandEntityList_3 = List.of(
                new GrandEntity(31, "grandEntity-3-1"),
                new GrandEntity(32, "grandEntity-3-2"),
                new GrandEntity(33, "grandEntity-3-3")
        );

        One2OneEntity one2OneEntity_1 = new One2OneEntity("wenben-1", 100);
        One2OneEntity one2OneEntity_2 = new One2OneEntity("wenben-2", 200);
        One2OneEntity one2OneEntity_3 = new One2OneEntity("wenben-3", 300);

        List<SubEntity> subEntityList = List.of(
                new SubEntity(Boolean.TRUE, 111, "subEntity-1-1", one2OneEntity_1, grandEntityList_1),
                new SubEntity(Boolean.FALSE, 222, "subEntity-1-2", one2OneEntity_2, grandEntityList_2),
                new SubEntity(Boolean.TRUE, 333, "subEntity-1-3", one2OneEntity_3, grandEntityList_3)
        );

        MasterEntity aggregateRoot = new MasterEntity(
                Boolean.FALSE,
                new Date(),
                212,
                TestEnum.ONE,
                List.of(TestEnum.TWO, TestEnum.ONE, TestEnum.ONE),
                subEntityList
        );

        ErrorStructureGenerator generator = ErrorStructureGenerator.from(aggregateRoot);
        generator.addErrorObject(aggregateRoot, "ceshibuerzhi", "长度超过100");
        generator.addErrorObject(aggregateRoot, "ceshiriqishijian", "日期时间格式不正确");
        SubEntity firstSubEntity = aggregateRoot.getCeshizishitiDtoList().get(0);
        generator.addErrorObject(firstSubEntity, "zishitibuerzhi", "你的布尔值错了");
        generator.addErrorObject(firstSubEntity.getYiduiduo().get(1), "zhenshuzhiduan", "整数字段错误");
        generator.addErrorObject(firstSubEntity.getYiduiyi(), "wenben", "文本字段错误");
        generator.addErrorObject(aggregateRoot.getCeshizishitiDtoList().get(1), "zishitishuzi", "子实体数字也不对");

        Map<String, Object> errorStructure = generator.generate();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> originMap = mapper.readValue(jsonStructure, new TypeReference<>() {});
        assertThat(errorStructure).isEqualTo(originMap);
    }

    @Data
    @AllArgsConstructor
    static class MasterEntity {
        private Boolean ceshibuerzhi;
        private Date ceshiriqishijian;
        private Integer integer;
        private TestEnum singleEnum;
        private List<TestEnum> enumList;
        private List<SubEntity> ceshizishitiDtoList;
    }

    enum TestEnum {
        ONE, TWO
    }

    @Data
    @AllArgsConstructor
    static class SubEntity {
        private Boolean zishitibuerzhi;
        private Integer zishitishuzi;
        private String string;
        private One2OneEntity yiduiyi;
        private List<GrandEntity> yiduiduo;
    }

    @Data
    @AllArgsConstructor
    static class One2OneEntity {
        private String wenben;
        private Integer integer;
    }

    @Data
    @AllArgsConstructor
    static class GrandEntity {
        private Integer zhenshuzhiduan;
        private String str;
    }
}
