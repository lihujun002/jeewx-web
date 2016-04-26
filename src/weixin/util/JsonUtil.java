package weixin.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.Gson;

public class JsonUtil {
    
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    
    private static Gson gson;
    
    private static ObjectMapper mapper;
    
    static {
        mapper = new ObjectMapper();
        gson = new Gson();
        mapper.getSerializationConfig().with(new SimpleDateFormat("yyyyMMddHHmmss"));
        // 空值处理为空串
        mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            
            @Override
            public void serialize(Object value, JsonGenerator jg, SerializerProvider sp)
                throws IOException, JsonProcessingException {
                jg.writeString("");
            }
        });
    }
    
    public static <T> String toJson(T obj) {
        String serialValue = null;
        try {
            serialValue = mapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e) {
            logger.error("class:{} instance serial to Json has error:{}", obj.toString(), e.getMessage().toString());
        }
        return serialValue;
    }
    
    public static <T> T fromStr(String json, Class<T> clz) {
        T deSerialObj = null;
        try {
            deSerialObj = mapper.readValue(json, clz);
        }
        catch (IOException e) {
            logger.error("json:{} instance serial to Json has error:{}", json, e.getMessage().toString());
        }
        return deSerialObj;
    }
    
    public static <T> List<T> getListFormStr(String json, Class<T> clz) {
        return (List<T>)getCollectionFromStr(json, clz, List.class);
    }
    
    public static <T> Collection<T> getCollectionFromStr(String json, Class<T> clz, Class<?> cls) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(cls, clz);
        List<T> deSerialList = null;
        try {
            deSerialList = mapper.readValue(json, javaType);
        }
        catch (IOException e) {
            logger.error("json:{} instance serial to Json has error:{}", json, e.getMessage().toString());
        }
        return deSerialList;
    }
    
    public static <T> Map<Long, T> getMap(String src, Class<T> clz) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(HashMap.class, Long.class, clz);
        Map<Long, T> result = null;
        try {
            result = mapper.readValue(src, javaType);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static <T> Map<String, T> getMapsByStr(String src, Class<T> clz) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(HashMap.class, String.class, clz);
        Map<String, T> result = null;
        try {
            result = mapper.readValue(src, javaType);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 将Map转化为Json
     * 
     * @param map
     * @return String
     */
    public static <T> String mapToJson(Map<String, T> map) {
        return gson.toJson(map);
    }
    
    /**
     * json字符串转换为对象
     * 
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return gson.fromJson(jsonStr, clazz);
    }
    
    /**
     * 对象转换为json字符串
     * 
     * @param obj
     * @return
     */
    public static String objToJson(Object obj) {
        return gson.toJson(obj);
    }
    
    public static Gson getGson() {
        return gson;
    }
    
}
