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
package io.gravitee.resource.cache;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCacheManager;

import io.gravitee.resource.api.AbstractConfigurableResource;
import io.gravitee.resource.cache.configuration.CacheResourceConfiguration;
import io.gravitee.resource.cache.configuration.RedisConfiguration;
import io.gravitee.resource.cache.ehcache.EhCacheDelegate;
import io.gravitee.resource.cache.rediscache.RedisCacheDelegate;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

/**
 * @author David BRASSELY (david at gravitee.io)
 * @author GraviteeSource Team
 */
public class CacheResource extends AbstractConfigurableResource<CacheResourceConfiguration> {

    private final Logger LOGGER = LoggerFactory.getLogger(CacheResource.class);

    private CacheManager cacheManager;
    
    private Cache cache;

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        String cacheType = configuration().getCacheType();
        if (isEhcache(cacheType)) {
        	Configuration configuration = new Configuration();
            configuration.setName(configuration().getName());
            cacheManager = new CacheManager(configuration);

            CacheConfiguration cacheConfiguration = new CacheConfiguration();
            cacheConfiguration.setEternal(false);
            cacheConfiguration.setTimeToIdleSeconds(configuration().getTimeToIdleSeconds());
            cacheConfiguration.setTimeToLiveSeconds(configuration().getTimeToLiveSeconds());
            cacheConfiguration.setMaxEntriesLocalHeap(configuration().getMaxEntriesLocalHeap());
            cacheConfiguration.setName(configuration().getName());

            LOGGER.info("Create a new cache: {}", configuration().getName());
            net.sf.ehcache.Cache ehCache = new net.sf.ehcache.Cache(cacheConfiguration);
            cache = new EhCacheDelegate(ehCache);
            cacheManager.addCache(ehCache);

        } else if (isRediscache(cacheType)) {
        	Set cacheSet = new HashSet();
        	cacheSet.add(configuration().getName());
        	
        	long ttl = configuration().getTimeToLiveSeconds();
        	
        	RedisConfiguration redisConfig = new RedisConfiguration();
        	RedisCacheManager redisCacheManager = redisConfig.cacheManager(cacheSet, ttl);
        	org.springframework.cache.Cache redisCache = redisCacheManager.getCache(configuration().getName());
        	cache = new RedisCacheDelegate(redisCache);
        }
        
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        if (cacheManager != null) {
            LOGGER.info("Clear cache {}", configuration().getName());
            cacheManager.shutdown();
        }
    }

    public Cache getCache() {
        return this.cache;
    }
    
    private boolean isEhcache (String cacheType) {
    	return cacheType == "ehcache";
    }
    
    private boolean isRediscache (String cacheType) {
    	return cacheType == "redis";
    }
}
