package com.sidooo.senode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;

@Configuration
@EnableMongoRepositories
@ComponentScan({"com.sidooo.point","com.sidooo.seed"})
@PropertySource(value = "classpath:mongo.properties")
public class MongoConfiguration {
	
	@Value("${mongodb.host}")
	private String mongoHost;

	@Value("${mongodb.port}")
	private int mongoPort;

	@Value("${mongodb.database}")
	private String mongoDbname;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception {
		// UserCredentials userCredentials = new UserCredentials("joe",
		// "secret");
		return new SimpleMongoDbFactory(new Mongo(mongoHost, mongoPort),
				mongoDbname);
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}

}
