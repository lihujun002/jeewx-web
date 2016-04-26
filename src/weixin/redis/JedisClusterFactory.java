package weixin.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jeecgframework.core.util.ResourceUtil;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * 
 * @author lihj17
 *         
 */
@Configuration
@Component
public class JedisClusterFactory implements FactoryBean<JedisCluster>, InitializingBean {
    
    private JedisCluster jedisCluster;
    
    private String timeout;
    
    private String maxRedirections;
    
    private String[] addresses;
    
    public void setAddresses(String addresses) {
        this.addresses = addresses.split(";");
    }
    
    private GenericObjectPoolConfig genericObjectPoolConfig;
    
    private String maxWaitMillis;
    
    private String maxTotal;
    
    private String minIdle;
    
    private String maxIdle;
    
    private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");
    
    private int lastTotal = 0;
    
    @Override
    public JedisCluster getObject()
        throws Exception {
        return jedisCluster;
    }
    
    @Override
    public Class<? extends JedisCluster> getObjectType() {
        return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    private Set<HostAndPort> parseHostAndPort()
        throws Exception {
        maxWaitMillis = ResourceUtil.getRedisConfig("system.redis.maxWaitMillis");
        maxTotal = ResourceUtil.getRedisConfig("system.redis.maxTotal");
        minIdle = ResourceUtil.getRedisConfig("system.redis.minIdle");
        maxIdle = ResourceUtil.getRedisConfig("system.redis.maxIdle");
        timeout = ResourceUtil.getRedisConfig("system.redis.timeout");
        maxRedirections = ResourceUtil.getRedisConfig("system.redis.maxRedirections");
        setAddresses(ResourceUtil.getRedisConfig("system.redis.address"));
        
        this.genericObjectPoolConfig = new GenericObjectPoolConfig();
        this.genericObjectPoolConfig.setMinIdle(Integer.valueOf(minIdle));
        this.genericObjectPoolConfig.setMaxIdle(Integer.valueOf(maxIdle));
        this.genericObjectPoolConfig.setMaxTotal(Integer.valueOf(maxTotal));
        this.genericObjectPoolConfig.setMaxWaitMillis(Long.valueOf(maxWaitMillis));
        this.genericObjectPoolConfig.setBlockWhenExhausted(false);
        this.genericObjectPoolConfig.setTestOnBorrow(false);
        this.genericObjectPoolConfig.setTestOnCreate(false);
        this.genericObjectPoolConfig.setTestOnReturn(false);
        this.genericObjectPoolConfig.setTestWhileIdle(true);
        Set<HostAndPort> haps = new HashSet<HostAndPort>();
        for (String address : this.addresses) {
            boolean isIpPort = p.matcher(address).matches();
            if (isIpPort) {
                String[] ipAndPort = address.split(":");
                HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                haps.add(hap);
            }
        }
        return haps;
    }
    
    @Override
    public void afterPropertiesSet()
        throws Exception {
        Set<HostAndPort> haps = this.parseHostAndPort();
        if (this.lastTotal == 0 || lastTotal != haps.size()) {
            this.lastTotal = haps.size();
            this.jedisCluster = new JedisCluster(haps, Integer.valueOf(timeout), Integer.valueOf(maxRedirections),
                genericObjectPoolConfig);
        }
    }
}