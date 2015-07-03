package com.sidooo.senode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ComponentScan({"com.sidooo.division"})
@PropertySource(value = "classpath:rediis.properties")
public class RedisConfiguration {
	
	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private int redisPort;
	
	@Bean
	public StringRedisTemplate redisTemplate() {
		StringRedisTemplate template = new StringRedisTemplate(
				redisConnectionFactory());
		// template.setEnableTransactionSupport(true);
		return template;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory redis = new JedisConnectionFactory();
		redis.setHostName(redisHost);
		redis.setPort(redisPort);
		return redis;
	}
}
