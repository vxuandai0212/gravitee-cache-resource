package io.gravitee.resource.cache.rediscache;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisTest1 {
	private Jedis jedis;
	@Before
	public void setUpCache() {
		//Connecting to Redis server on localhost 
	      Jedis jedis = new Jedis("localhost"); 
	      System.out.println("Connection to server sucessfully"); 
	      //set the data in redis string 
	      jedis.set("tutorial-name", "Redis tutorial"); 
	      // Get the stored data and print it 
	      System.out.println("Stored string in redis:: "+ jedis.get("tutorial-name"));
	}
	
	@Test
	public void testConnection() {
		assertEquals(true, jedis.isConnected());
		
//		jedis.set("localhost", "127.0.0.1");
//		jedis.get("localhost");
	}
}
