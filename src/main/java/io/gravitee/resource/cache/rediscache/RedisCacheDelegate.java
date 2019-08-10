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

import io.gravitee.resource.cache.Cache;
import io.gravitee.resource.cache.Element;

public class RedisCacheDelegate implements Cache {

    private final org.springframework.cache.Cache rediscache;
    private final Logger LOGGER = LoggerFactory.getLogger(RedisCacheDelegate.class);

    public RedisCacheDelegate(org.springframework.cache.Cache rediscache) {
        this.rediscache = rediscache;
    }

    @Override
    public String getName() {
        return rediscache.getName();
    }

    @Override
    public Object getNativeCache() {
        return rediscache;
    }

    @Override
    public Element get(Object key) {
    	LOGGER.info("The element key: " + key + " is: " + rediscache.get(key));
        return (Element) rediscache.get(key);
    }

    @Override
    public void put(Element element) {
    	LOGGER.info("Put element key: " + element.key().toString() + " is: " + element);
    	rediscache.put(element.key().toString(), element.value());
    }

    @Override
    public void evict(Object key) {
    	rediscache.evict(key);
    }

    @Override
    public void clear() {
    	rediscache.clear();
    }
}