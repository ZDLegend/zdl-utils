package zdl.util.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 基于Jackson的json处理工具类
 * Created by ZDLegend on 2020/9/9 11:22
 */
public class JsonUtils {

    private static final ObjectMapper objectMapper;
    private static final ObjectMapper snakeCaseObjectMapper;

    static {
        objectMapper = new Jackson2ObjectMapperBuilder()
                .failOnUnknownProperties(false)
                .failOnEmptyBeans(false)
                .build();

        snakeCaseObjectMapper = new Jackson2ObjectMapperBuilder()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .failOnUnknownProperties(false)
                .failOnEmptyBeans(false)
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .build();
    }

    private JsonUtils() {

    }

    /**
     * json字符串转对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new JsonTransException(e);
        }
    }

    /**
     * json字符串转对象
     */
    public static <T> T readValue(String json, TypeReference<T> reference) {
        try {
            return objectMapper.readValue(json, reference);
        } catch (IOException e) {
            throw new JsonTransException(e);
        }
    }

    public static <T> T parseObject(byte[] src, Class<T> clazz) {
        try {
            return objectMapper.readValue(src, clazz);
        } catch (IOException e) {
            throw new JsonTransException(e);
        }
    }

    public static <T> T readValue(byte[] src, TypeReference<T> reference) {
        try {
            return objectMapper.readValue(src, reference);
        } catch (IOException e) {
            throw new JsonTransException(e);
        }
    }

    public static <T> T parseObject(InputStream src, Class<T> clazz) {
        try {
            return objectMapper.readValue(src, clazz);
        } catch (IOException e) {
            throw new JsonTransException(e);
        }
    }

    public static <T> T readValue(InputStream src, TypeReference<T> reference) {
        try {
            return objectMapper.readValue(src, reference);
        } catch (IOException e) {
            throw new JsonTransException(e);
        }
    }

    /**
     * json字符串转对象数组
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return objectMapper.readValue(json, listType);
        } catch (IOException e) {
            throw new JsonTransException(e);
        }
    }

    public static <T> List<T> parseArray(InputStream src, Class<T> clazz) {
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return objectMapper.readValue(src, listType);
        } catch (IOException e) {
            throw new JsonTransException(e);
        }
    }

    public static <T> String writeValueAsString(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JsonTransException(e);
        }
    }

    public static <T> byte[] writeValueAsByte(T value) {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new JsonTransException(e);
        }
    }

    /**
     * 校检接送字符串参数是否符合实体规范
     */
    public static <T> T validation(String json, Class<T> clazz) {
        T target = parseObject(json, clazz);
        validation(target);
        return target;
    }

    public static <T> void validation(T target) {
        Set<ConstraintViolation<T>> violations = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(target);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new ConstraintViolationException(violations);
        }
    }
}
