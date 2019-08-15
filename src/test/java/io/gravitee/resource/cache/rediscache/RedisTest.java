package io.gravitee.resource.cache.rediscache;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.WeakHashMap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import io.gravitee.resource.cache.Cache;
import io.gravitee.resource.cache.Element;
import io.gravitee.resource.cache.Item;
import io.gravitee.resource.cache.configuration.CacheItem;
import io.gravitee.resource.cache.configuration.CacheElement;
import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.MediaType;
import io.gravitee.policy.cache.CacheResponse;
import io.gravitee.resource.cache.configuration.RedisConf;

public class RedisTest {
	private Cache redisCache;

	@Before
	public void setUpCache() {
//		String KEY = "ITEM";
		RedisConf rf = new RedisConf();
		RedisTemplate<String, Element> redisTemplate = rf.redisTemplate();
		redisTemplate.getConnectionFactory().getConnection().ping();
//		Item item = new Item(1, "Mr.T", "music");
//		redisTemplate.opsForHash().put(KEY,item.getId(),item);;
//		redisTemplate.opsForHash().get(KEY, 1);
		
		
//		CacheElement cr = new CacheElement(redisTemplate);
//		this.redisCache = cr;
//		CacheResponse cacheResponse = new CacheResponse();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("key2", "value2");
//		cacheResponse.setHeaders(headers);
//		CacheItem ci = new CacheItem("abc", cacheResponse);	
//		this.redisCache.put(ci);
	}

	@Test
	public void testGetValue() {
		assertEquals("123", redisCache.get("abc"));
	}
}
