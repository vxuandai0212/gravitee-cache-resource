package io.gravitee.resource.cache.configuration;

import redis.clients.jedis.Jedis;

public class ResConf {
	
	
	public void test() {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.set("localhost", "127.0.0.1");
		jedis.get("localhost");
	}

}
