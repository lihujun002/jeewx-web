package weixin.redis;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisCluster;
import weixin.util.JsonUtil;

/**
 * 
 * @author lihj17
 *         
 */
@Component
public class JedisTemplate {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JedisClusterFactory jedisClusterJdbcFactory;
    
    public JedisCluster getJedisCluster()
        throws Exception {
        return jedisClusterJdbcFactory.getObject();
    }
    
    public boolean merge(String key, String value)
        throws Exception {
        String result = getJedisCluster().set(key.toLowerCase(), value);
        return "ok".equalsIgnoreCase(result);
    }
    
    public boolean delete(String key)
        throws Exception {
        long result = getJedisCluster().del(key.toLowerCase());
        return result > 0;
    }
    
    public String get(String key)
        throws Exception {
        return getJedisCluster().get(key.toLowerCase());
    }
    
    public Long expire(String key, int seconds)
        throws Exception {
        return getJedisCluster().expire(key.toLowerCase(), seconds);
    }
    
    public Map<String, String> hgetAll(String key)
        throws Exception {
        return getJedisCluster().hgetAll(key.toLowerCase());
    }
    
    public String hget(String key, String field)
        throws Exception {
        return getJedisCluster().hget(key.toLowerCase(), field);
    }
    
    public Long hset(String key, String field, String value)
        throws Exception {
        return getJedisCluster().hset(key.toLowerCase(), field, value);
    }
    
    public boolean set(final String key, final String value)
        throws Exception {
        if (StringUtils.isBlank(key)) {
            if (logger.isWarnEnabled()) {
                logger.warn("set redis cache the null. key: {}, value: {} ", key, value);
            }
            return false;
        }
        String valueJson = null;
        try {
            valueJson = JsonUtil.toJson(value);
            getJedisCluster().set(key, value);
            if (logger.isDebugEnabled()) {
                logger.debug("set redis cache key: {}, value: {}", key, valueJson);
            }
        }
        catch (DataAccessException e) {
            if (logger.isErrorEnabled()) {
                logger.error("set key:{} error, reason : {} ", key, e);
            }
            throw e;
        }
        return true;
    }
    
    private List<Field> getClassFields(List<Field> fields, Class<?> clazz) {
        Field[] fs = clazz.getDeclaredFields();
        for (Field field : fs) {
            fields.add(field);
        }
        if (clazz.getSuperclass() != null) {
            getClassFields(fields, clazz.getSuperclass());
        }
        return fields;
    }
    
    public List<String> hgetMany(String key, String... fields)
        throws Exception {
        return getJedisCluster().hmget(key.toLowerCase(), fields);
    }
    
    public String hsetMany(String key, Map<String, String> hash)
        throws Exception {
        return getJedisCluster().hmset(key.toLowerCase(), hash);
    }
    
}
