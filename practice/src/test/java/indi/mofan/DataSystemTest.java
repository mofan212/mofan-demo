package indi.mofan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static indi.mofan.AttrCategory.COMPOSITE;
import static indi.mofan.AttrType.LIST;
import static indi.mofan.AttrType.OBJECT;

/**
 * @author mofan
 * @date 2022/10/20 16:26
 */
public class DataSystemTest {

    private List<DataTransferObject> initDto() {
        List<DataTransferObject> list = new ArrayList<>();
        DataTransferObject root = new DataTransferObject();
        root.setId("A");
        List<Attribute> rootAttrs = Arrays.asList(
                new Attribute("B", OBJECT),
                new Attribute("C", LIST),
                new Attribute("J", OBJECT),
                new Attribute("D", LIST)
        );
        root.setAttrs(rootAttrs);
        list.add(root);

        DataTransferObject bDto = new DataTransferObject();
        bDto.setId("B");
        List<Attribute> bAttrs = Arrays.asList(
                new Attribute("E", LIST),
                new Attribute("F", OBJECT)
        );
        bDto.setAttrs(bAttrs);
        list.add(bDto);

        DataTransferObject cDto = new DataTransferObject();
        cDto.setId("C");
        List<Attribute> cAttrs = Arrays.asList(
                new Attribute("G", LIST),
                new Attribute("H", OBJECT)
        );
        cDto.setAttrs(cAttrs);
        list.add(cDto);

        DataTransferObject jDto = new DataTransferObject();
        jDto.setId("J");
        jDto.setAttrs(Collections.singletonList(new Attribute("K", OBJECT)));
        list.add(jDto);

        DataTransferObject dDto = new DataTransferObject();
        dDto.setId("D");
        dDto.setAttrs(Collections.singletonList(new Attribute("I", LIST)));
        list.add(dDto);

        DataTransferObject hDto = new DataTransferObject();
        hDto.setId("H");
        List<Attribute> hAttrs = Arrays.asList(
                new Attribute("M", OBJECT),
                new Attribute("N", OBJECT)
        );
        hDto.setAttrs(hAttrs);
        list.add(hDto);

        list.add(new DataTransferObject("E"));
        list.add(new DataTransferObject("F"));
        list.add(new DataTransferObject("G"));
        list.add(new DataTransferObject("I"));
        list.add(new DataTransferObject("K"));
        list.add(new DataTransferObject("M"));
        list.add(new DataTransferObject("N"));

        return list;
    }

    /**
     * 体系的含义：
     * 1. 一对一的数据，认为是同一体系的；
     * 2. 一对多的数据，且两者在纵向同一关系线上，认为是同一体系；
     * 3. 包含同一层级的多个一对多的数据，不认为它们在同一体系上。
     */
    @Test
    public void testSystemInfo() {
        List<DataTransferObject> dtoList = initDto();
        // 需要提供全量的 DtoMap
        Map<String, DataTransferObject> dtoMap = dtoList.stream().collect(Collectors.toMap(DataTransferObject::getId, Function.identity()));
        String rootId = "A";
        Set<String> aLine = getCommonSystemLine(dtoMap, rootId);
        Set<String> rootAttrs = getAttrByPredicate(dtoMap.get(rootId), i -> true);

        // 结果集合
        List<Set<String>> systemInfo = new ArrayList<>();

        // 主栈
        Deque<Set<String>> main = new ArrayDeque<>();
        main.push(aLine);
        // 辅栈
        Deque<Set<String>> secondary = new ArrayDeque<>();
        secondary.push(rootAttrs);

        while (!main.isEmpty()) {
            Set<String> attrs = secondary.peek();
            if (CollectionUtils.isNotEmpty(attrs)) {
                Optional<String> any = attrs.stream().findAny();
                if (any.isPresent()) {
                    String dtoType = any.get();
                    Set<String> commonLine = getCommonSystemLine(dtoMap, dtoType);
                    main.push(commonLine);
                    attrs.remove(dtoType);

                    Set<String> childAttrs = getAttrByPredicate(dtoMap.get(dtoType), i -> true);
                    if (CollectionUtils.isNotEmpty(childAttrs)) {
                        secondary.push(childAttrs);
                    } else {
                        Set<String> set = main.stream().flatMap(Set::stream).collect(Collectors.toSet());
                        systemInfo.add(set);
                        main.pop();
                    }
                }
            } else {
                main.pop();
                secondary.pop();
            }
        }

        Set<Set<String>> temp = new HashSet<>();

        // 去除集合中存在的子集
        systemInfo.forEach(i -> systemInfo.forEach(j -> {
            if (i != j && i.containsAll(j)) {
                temp.add(j);
            }
        }));
        systemInfo.removeAll(temp);

        systemInfo.forEach(System.out::println);
    }

    private Set<String> getCommonSystemLine(Map<String, DataTransferObject> dtoMap, String rootId) {
        DataTransferObject root = dtoMap.get(rootId);
        Set<String> one2one = getAttrByPredicate(root, i -> OBJECT.equals(i.getType()));
        Set<String> commonSystemLine = new HashSet<>(one2one);
        Deque<String> one2oneStack = new ArrayDeque<>(one2one);
        while (!one2oneStack.isEmpty()) {
            String pop = one2oneStack.pop();
            commonSystemLine.add(pop);
            DataTransferObject dto = dtoMap.get(pop);
            Set<String> one2OneAttr = getAttrByPredicate(dto, i -> OBJECT.equals(i.getType()));
            one2oneStack.addAll(one2OneAttr);
        }
        // 把自己再加上去
        commonSystemLine.add(rootId);
        return commonSystemLine;
    }

    private Set<String> getAttrByPredicate(DataTransferObject dto, Predicate<Attribute> predicate) {
        return dto.getAttrs().stream()
                .filter(i -> COMPOSITE.equals(i.getCategory()))
                .filter(predicate)
                .map(Attribute::getDataType)
                .collect(Collectors.toSet());
    }

    @Test
    @SneakyThrows
    public void testFlatSourceData() {
        Master master = new Master();
        List<Son> sons = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Son son = new Son();
            List<GrandSon> grandSons = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                GrandSon grandSon = new GrandSon();
                grandSons.add(grandSon);
            }
            son.setGrandSons(grandSons);
            sons.add(son);
        }
        master.setSons(sons);

        List<Map<String, Object>> fullMap = new ArrayList<>();
        Map<String, Object> masterMap = Collections.singletonMap("master", new Master());
        for (Son son : master.getSons()) {
            Map<String, Object> sonMap = new HashMap<>(masterMap);
            sonMap.put("son", son);

            for (GrandSon grandSon : son.getGrandSons()) {
                Map<String, Object> grandsonMap = new HashMap<>(sonMap);
                grandsonMap.put("grandson", grandSon);

                // end
                fullMap.add(grandsonMap);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fullMap));
    }

    @Setter
    @Getter
    private static class Master implements Serializable {
        private static final long serialVersionUID = -4889525555226587376L;
        List<Son> sons;
    }

    @Getter
    @Setter
    private static class Son implements Serializable  {
        private static final long serialVersionUID = -1925719122684448332L;
        List<GrandSon> grandSons;
    }

    private static class GrandSon implements Serializable  {

        private static final long serialVersionUID = -8001138811520305599L;
    }

    @Test
    public void testGroupBySubset() {
        List<String> aList = new ArrayList<>(Arrays.asList("B", "C", "D", "E"));
        List<String> bList = new ArrayList<>(Arrays.asList("B", "C"));
        List<String> cList = new ArrayList<>(Collections.singletonList("M"));
        List<String> dList = new ArrayList<>(Arrays.asList("M", "N"));
        List<String> eList = new ArrayList<>(Arrays.asList("B", "C", "D", "E"));

        List<List<String>> source = new ArrayList<>(Arrays.asList(aList, bList, cList, dList, eList));

        List<List<List<String>>> result = new ArrayList<>();

        Map<String, List<List<String>>> tempMap = source.stream().collect(Collectors.groupingBy(i -> String.join("_", i)));
        Set<String> tempMapKetSet = tempMap.keySet();

        List<String> subsetList = new ArrayList<>();

        tempMapKetSet.forEach(i -> tempMapKetSet.forEach(j -> {
            if (!i.equals(j) && i.contains(j)) {
                subsetList.add(j);
            }
        }));

        List<String> fullKeyList = tempMapKetSet.stream().filter(i -> !subsetList.contains(i)).collect(Collectors.toList());
        for (String fullKey : fullKeyList) {
            List<List<String>> collect = tempMap.entrySet().stream().filter(i -> fullKey.contains(i.getKey()))
                    .map(Map.Entry::getValue)
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
            result.add(collect);
        }

        result.forEach(System.out::println);
    }

}
