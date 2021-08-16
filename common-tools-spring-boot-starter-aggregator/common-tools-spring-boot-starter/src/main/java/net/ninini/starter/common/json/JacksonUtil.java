package net.ninini.starter.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: JacksonUtil
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/9 14:56
 * @Version 1.0.0
 */
@Slf4j
public class JacksonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @title writeAsString
     * @description todo 对象转换成String
     * @param: t
     * @return: java.lang.String
     * @author HanYu
     * @updateTime 2021/7/9 15:12
     */
    public static <T> String writeAsString(T t) throws JsonProcessingException {
        String r;
        try {
            r = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error("JacksonUtil JSON writeAsString error", e);
            throw e;
        }
        return r;
    }

    public static <T> T readValue(String src, Class<T> t) throws JsonProcessingException {
        return objectMapper.readValue(src, t);
    }

    public static <T> T readValue(InputStream src, Class<T> t) throws IOException {
        return objectMapper.readValue(src, t);
    }

    public static <T> Map<String, T> readValueToMap(InputStream src, Class<T> t) throws IOException {
        return objectMapper.readValue(src, new TypeReference<Map<String, T>>() {
        });
    }

    public static <T> List<T> readValueToList(InputStream src, Class<T> t) throws IOException {
        return objectMapper.readValue(src, new TypeReference<List<T>>() {
        });
    }


    @Deprecated
    public static <T> List<T> readValueToList(String json, Class<T> c) {
        try {
            JavaType javaType = getCollectionType(List.class, c);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }


}
