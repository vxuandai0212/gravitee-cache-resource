package io.gravitee.resource.cache.configuration;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePool;


public class RedisConfiguration {
   @Value("$cacheresource.redis.host}")
   private String host;
   
   @Value("${cacheresource.redis.port}")
   private int port;
   
   @Value("${cacheresource.redis.password}")
   private String password;

   public LettuceConnectionFactory redisConnectionFactory() {
	RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
	redisConf.setHostName(host);
	redisConf.setPort(port);
	redisConf.setPassword(RedisPassword.of(password));	    
        return new LettuceConnectionFactory(redisConf);
   }
   
   public RedisCacheConfiguration cacheConfiguration(long duration) {
	RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
	  .entryTtl(Duration.ofSeconds(duration))
	  .disableCachingNullValues();	
	return cacheConfig;
   }
   

   
   public RedisCacheManager cacheManager(Set<String> cacheNames, long ttl) {
	RedisCacheManager rcm = RedisCacheManager.builder(redisConnectionFactory())
	  .cacheDefaults(cacheConfiguration(ttl))
	  .initialCacheNames(cacheNames)
	  .transactionAware()
	  .build();
	return rcm;
   }  
} 