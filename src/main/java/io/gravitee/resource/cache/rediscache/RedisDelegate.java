/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.resource.cache.rediscache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import io.gravitee.resource.cache.Cache;
import io.gravitee.resource.cache.Element;
import io.gravitee.resource.cache.rediscache.RedisElement;

public class RedisDelegate implements Cache {

    private final RedisTemplate<String,Element> redisTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(RedisDelegate.class);
    private static String REDIS_KEY = "Redis";

    public RedisDelegate(RedisTemplate<String,Element> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getName() {
        return "my-redis";
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public Element get(Object key) {
    	return (Element) this.redisTemplate.opsForHash().get(REDIS_KEY, key);
    }

    @Override
    public void put(Element element) {
    	this.redisTemplate.opsForHash().put(REDIS_KEY, element.key(), element);
    }

    @Override
    public void evict(Object key) {
    	this.redisTemplate.opsForHash().delete(REDIS_KEY, key);  
    }

    @Override
    public void clear() {
    	redisTemplate.execute((RedisCallback<Object>) connection -> {
			connection.flushDb();
			return null;
		});
    }
}