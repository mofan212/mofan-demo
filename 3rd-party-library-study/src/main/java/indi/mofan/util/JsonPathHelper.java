package indi.mofan.util;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author mofan
 * @date 2023/3/1 16:26
 */
@Slf4j
public final class JsonPathHelper {
    private JsonPathHelper() {
    }

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
    }

    public static DocumentContext parse(String json) {
        return JsonPath.parse(json);
    }

    public static <T> T readPath(String json, String path, Class<T> clazz, T defaultValue) {
        return readPath(JsonPath.parse(json), path, clazz, defaultValue);
    }

    public static <T> T readPath(DocumentContext document, String path, Class<T> clazz, T defaultValue) {
        try {
            return document.read(path, clazz);
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage() + " " + path);
            return defaultValue;
        }
    }

    public static <T> T readPath(String json, String path, TypeRef<T> typeRef, T defaultValue) {
        return readPath(JsonPath.parse(json), path, typeRef, defaultValue);
    }

    public static <T> T readPath(DocumentContext document, String path, TypeRef<T> typeRef, T defaultValue) {
        try {
            return document.read(path, typeRef);
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage() + " " + path);
            return defaultValue;
        }
    }
}