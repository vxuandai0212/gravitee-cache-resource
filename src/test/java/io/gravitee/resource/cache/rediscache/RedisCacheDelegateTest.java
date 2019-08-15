package io.gravitee.resource.cache.rediscache;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
//import org.junit.Test;
import org.springframework.data.redis.cache.RedisCacheManager;

import io.gravitee.resource.cache.configuration.RedisConfiguration;

public class RedisCacheDelegateTest {
	private RedisCacheDelegate redisCache;

	@Before
	public void setUpCache() {
		String cacheName = "my-redis";
		Set cacheSet = new HashSet();
		cacheSet.add(cacheName);

		long ttl = 100;

		RedisConfiguration redisConfig = new RedisConfiguration();
		RedisCacheManager redisCacheManager = redisConfig.cacheManager(cacheSet, ttl);
		org.springframework.cache.Cache cache = redisCacheManager.getCache(cacheName);
		
		redisCache = new RedisCacheDelegate(cache);
	}

	@Test
	public void testGetValue() {
		assertEquals("abc", redisCache.get("123"));
	}

}
