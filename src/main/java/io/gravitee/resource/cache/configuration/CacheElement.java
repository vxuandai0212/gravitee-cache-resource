package io.gravitee.resource.cache.configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import io.gravitee.resource.cache.Element;
import io.gravitee.resource.cache.Cache;

public class CacheElement implements Cache {
    private RedisTemplate<String,Element> redisTemplate;
    private HashOperations<String,Object,Element> hashOperation;
    private ValueOperations<String,Element> valueOperations;
    
    public CacheElement(RedisTemplate<String,Element> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.hashOperation = redisTemplate.opsForHash();
        this.valueOperations = redisTemplate.opsForValue();
    }
    
    public void putMap(String redisKey,Object key,Element data) {
        hashOperation.put(redisKey, key, data);
    }
    
    public Element getMapAsSingleEntry(String redisKey,Element key) {
        return  hashOperation.get(redisKey,key);
    }
     
    public Map<Object, Element> getMapAsAll(String redisKey) {
        return hashOperation.entries(redisKey);
    }
    
    public void putValue(String key,Element value) {
        valueOperations.set(key, value);
    }
     
    public void putValueWithExpireTime(String key,Element value,long timeout,TimeUnit unit) {
        valueOperations.set(key, value, timeout, unit);
    }
     
    public Element getValue(String key) {
        return valueOperations.get(key);
    }
     
    public void setExpire(String key,long timeout,TimeUnit unit) {
       redisTemplate.expire(key, timeout, unit);
    }

	@Override
	public String getName() {
		return "my-redis";
	}

	@Override
	public Object getNativeCache() {
		return this;
	}

	@Override
	public Element get(Object key) {
		try {
			Element element = valueOperations.get(key);
	        return (element == null) ? null : element;
		} catch(Exception e) {
			System.err.println(e);
		}
		return null;
	}

	@Override
	public void put(Element element) {
		try {
			valueOperations.set(element.key().toString(), element);
		} catch(Exception e) {
			System.err.println(e);
		}
	}

	@Override
	public void evict(Object key) {
		redisTemplate.delete(key.toString());
		
	}

	@Override
	public void clear() {
		redisTemplate.execute((RedisCallback<Object>) connection -> {
			connection.flushDb();
			return null;
		});
	}
}