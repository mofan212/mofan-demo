package indi.mofan.util;

import cn.hutool.core.bean.BeanPath;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author mofan
 * @date 2023/3/1 16:26
 */
@Slf4j
public final class JsonPathHelper {
    private JsonPathHelper() {
    }

    private static final Configuration AS_PATH_LIST_CONF;

    static {
        // 与 SpringBoot 整合时，可以将这些配置写到自定义 Runner 中
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });

        AS_PATH_LIST_CONF = com.jayway.jsonpath.Configuration.defaultConfiguration()
                .jsonProvider(Configuration.defaultConfiguration().jsonProvider())
                .mappingProvider(Configuration.defaultConfiguration().mappingProvider())
                .addOptions(Option.AS_PATH_LIST);
    }

    public static Set<String> getPath(String json, String path, Predicate... predicates) {
        return getPath(parse(json), path, predicates);
    }

    public static Set<String> getPath(DocumentContext document, String path, Predicate... predicates) {
        try {
            if (Objects.isNull(document)) {
                return Collections.emptySet();
            }
            Object json = document.json();
            List<String> detailedPath = JsonPath.using(AS_PATH_LIST_CONF).parse(json).read(path, predicates);
            return new LinkedHashSet<>(detailedPath);
        } catch (Exception e) {
            log.warn("{} {}", e.getLocalizedMessage(), path);
            return Collections.emptySet();
        }
    }

    public static List<String> convert2PathList(String JSONPath) {
        return new ArrayList<>(BeanPath.create(JSONPath).getPatternParts());
    }

    public static DocumentContext parse(String json) {
        return JsonPath.parse(json);
    }

    public static <T> T readPath(String json, String path, T defaultValue, Predicate... predicates) {
        if (StringUtils.isEmpty(json)) {
            return defaultValue;
        }
        return readPath(JsonPath.parse(json), path, defaultValue, predicates);
    }

    public static <T> T readPath(DocumentContext document, String path, T defaultValue, Predicate... predicates) {
        try {
            return document.read(path, predicates);
        } catch (Exception e) {
            log.warn("{} {}", e.getLocalizedMessage(), path);
            return defaultValue;
        }
    }

    public static <T> T readPath(String json, String path, Class<T> clazz, T defaultValue, Predicate... predicates) {
        if (StringUtils.isEmpty(json)) {
            return defaultValue;
        }
        return readPath(JsonPath.parse(json), path, clazz, defaultValue, predicates);
    }

    public static <T> T readPath(DocumentContext document, String path, Class<T> clazz, T defaultValue, Predicate... predicates) {
        try {
            return document.read(path, clazz, predicates);
        } catch (Exception e) {
            log.warn("{} {}", e.getLocalizedMessage(), path);
            return defaultValue;
        }
    }

    public static <T> T readPath(String json, String path, TypeRef<T> typeRef, T defaultValue) {
        if (StringUtils.isEmpty(json)) {
            return defaultValue;
        }
        return readPath(JsonPath.parse(json), path, typeRef, defaultValue);
    }

    public static <T> T readPath(DocumentContext document, String path, TypeRef<T> typeRef, T defaultValue) {
        try {
            return document.read(path, typeRef);
        } catch (Exception e) {
            log.warn("{} {}", e.getLocalizedMessage(), path);
            return defaultValue;
        }
    }

    public static <T> Map<String, T> getDetailedPathAndReadPath(DocumentContext document, String path, T defaultValue, Predicate... predicates) {
        Set<String> detailedPathSet = getPath(document, path, predicates);
        Map<String, T> result = Maps.newHashMapWithExpectedSize(detailedPathSet.size());

        for(String detailedPath : detailedPathSet) {
            T value = (T)readPath(document, detailedPath, defaultValue, predicates);
            result.put(detailedPath, value);
        }

        return result;
    }

    public static <T> Map<String, T> getDetailedPathAndReadPath(String json, String path, T defaultValue, Predicate... predicates) {
        return getDetailedPathAndReadPath(parse(json), path, defaultValue, predicates);
    }

    public static <T> Map<String, T> getDetailedPathAndReadPath(DocumentContext document, String path, TypeRef<T> typeRef, T defaultValue) {
        Set<String> detailedPathSet = getPath(document, path);
        Map<String, T> result = Maps.newHashMapWithExpectedSize(detailedPathSet.size());

        for(String detailedPath : detailedPathSet) {
            T value = (T)readPath(document, detailedPath, typeRef, defaultValue);
            result.put(detailedPath, value);
        }

        return result;
    }

    public static <T> Map<String, T> getDetailedPathAndReadPath(String json, String path, TypeRef<T> typeRef, T defaultValue) {
        return getDetailedPathAndReadPath(parse(json), path, typeRef, defaultValue);
    }

    public static <T> Map<String, T> getDetailedPathAndReadPath(DocumentContext document, String path, Class<T> clazz, T defaultValue, Predicate... predicates) {
        Set<String> detailedPathSet = getPath(document, path, predicates);
        Map<String, T> result = Maps.newHashMapWithExpectedSize(detailedPathSet.size());

        for(String detailedPath : detailedPathSet) {
            T value = (T)readPath(document, path, clazz, defaultValue, predicates);
            result.put(detailedPath, value);
        }

        return result;
    }

    public static <T> Map<String, T> getDetailedPathAndReadPath(String json, String path, Class<T> clazz, T defaultValue, Predicate... predicates) {
        return getDetailedPathAndReadPath(parse(json), path, clazz, defaultValue, predicates);
    }
}