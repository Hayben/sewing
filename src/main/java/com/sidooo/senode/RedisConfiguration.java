package com.sidooo.senode;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

@Configuration
@ComponentScan({"com.sidooo.counter"})
@PropertySource(value = "classpath:redis.properties")
public class RedisConfiguration {
	
	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private int redisPort;
	
	@Value("${redis.max.active}")
	private int redisMaxActive;
	
	@Value("${redis.max.idle}")
	private int redisMaxIdle;
	
	@Value("${redis.max.wait}")
	private int redisMaxWait;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
//	@Bean
//	public StringRedisTemplate redisTemplate() {
//		StringRedisTemplate template = new StringRedisTemplate(
//				redisConnectionFactory());
//		// template.setEnableTransactionSupport(true);
//		return template;
//	}
//
//	@Bean
//	public RedisConnectionFactory redisConnectionFactory() {
//		JedisConnectionFactory redis = new JedisConnectionFactory();
//		redis.setHostName(redisHost);
//		redis.setPort(redisPort);
//		return redis;
//	}
	
	@Bean
	public ShardedJedisPool  shardedJedisPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(redisMaxActive);
		config.setMaxIdle(redisMaxIdle);
		config.setMaxWaitMillis(1000 * redisMaxWait);
		
		JedisShardInfo info = new JedisShardInfo(redisHost, redisPort);
		List<JedisShardInfo> list = new ArrayList<JedisShardInfo>();
		list.add(info);
		return new ShardedJedisPool(config, list);
	}
}
