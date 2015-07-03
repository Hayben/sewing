package com.sidooo.senode;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan({"com.sidooo.queue"})
@PropertySource(value = "classpath:rabbit.properties")
public class RabbitConfiguration {
	
	@Value("${rabbit.host}")
	private String rabbitHost;
	
	@Value("${rabbit.port}")
	private int rabbitPort;
	
	@Value("${rabbit.user}")
	private String rabbitUser;
	
	@Value("${rabbit.password}")
	private String rabbitPassword;
	

	@Bean
	public CachingConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = 
				new CachingConnectionFactory(rabbitHost, rabbitPort);
		connectionFactory.setUsername(rabbitUser);
		connectionFactory.setPassword(rabbitPassword);
		return connectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		return new RabbitTemplate(connectionFactory());
	}
}
