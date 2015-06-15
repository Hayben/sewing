package com.sidooo.wheart;

import java.util.Properties;

import jcifs.smb.NtlmPasswordAuthentication;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.Mongo;

@Configuration
@EnableMongoRepositories
@EnableTransactionManagement
@ComponentScan(basePackages = "com.sidooo")
@PropertySource(value = "classpath:wbrain.properties")
public class DatawareConfiguration {

	@Value("${mongodb.host}")
	private String mongoHost;

	@Value("${mongodb.port}")
	private int mongoPort;

	@Value("${mongodb.database}")
	private String mongoDbname;

	@Value("${samba.host}")
	private String sambaHost;

	@Value("${samba.user}")
	private String sambaUser;

	@Value("${samba.password}")
	private String sambaPassword;

	@Value("${rabbit.host}")
	private String rabbitHost;
	
	@Value("${rabbit.port}")
	private int rabbitPort;
	
	@Value("${rabbit.user}")
	private String rabbitUser;
	
	@Value("${rabbit.password}")
	private String rabbitPassword;

	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private int redisPort;

	@Value("${mysql.host}")
	private String mysqlHost;

	@Value("${mysql.port}")
	private int mysqlPort;

	@Value("${mysql.database}")
	private String mysqlDbname;

	@Value("${mysql.user}")
	private String mysqlUser;

	@Value("${mysql.password}")
	private String mysqlPassword;

	// @Override
	// protected String getDatabaseName() {
	// // TODO Auto-generated method stub
	// return dbname;
	// }
	//
	// @SuppressWarnings("deprecation")
	// @Override
	// public Mongo mongo() throws Exception {
	// // TODO Auto-generated method stub
	// return new Mongo("db.sidooo.com", 27017);
	// }

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	//
	// MongoDB
	//
	// /////////////////////////////////////////////////////////////////////////////

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

	// /////////////////////////////////////////////////////////////////////////////
	//
	// Samba
	//
	// /////////////////////////////////////////////////////////////////////////////

	@Bean
	public NtlmPasswordAuthentication sambaAuth() {
		jcifs.Config.registerSmbURLHandler();
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
				sambaHost, sambaUser, sambaPassword);
		return auth;
	}

	// /////////////////////////////////////////////////////////////////////////////
	//
	// RabbitMQ
	//
	// /////////////////////////////////////////////////////////////////////////////
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

	// /////////////////////////////////////////////////////////////////////////////
	//
	// Redis
	//
	// /////////////////////////////////////////////////////////////////////////////

	@Bean
	public StringRedisTemplate redisTemplate() {
		StringRedisTemplate template = new StringRedisTemplate(
				redisConnectionFactory());
		// template.setEnableTransactionSupport(true);
		return template;
	}

	// @Bean
	// public PlatformTransactionManager transactionManager() throws
	// SQLException {
	// return new DataSourceTransactionManager(dataSource());
	// }

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory redis = new JedisConnectionFactory();
		redis.setHostName(redisHost);
		redis.setPort(redisPort);
		return redis;
	}

	// @Bean
	// public DataSource dataSource() throws SQLException{
	//
	// }

	// /////////////////////////////////////////////////////////////////////////////
	//
	// Mysql
	//
	// /////////////////////////////////////////////////////////////////////////////

	@Bean
	public SessionFactory sessionFactory() {
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(
				dataSource());
		builder.scanPackages("com.sidooo").addProperties(
				getHibernateProperties());
		return builder.buildSessionFactory();
	}

	private Properties getHibernateProperties() {
		Properties prop = new Properties();
		prop.put("hibernate.format_sql", "true");
		prop.put("hibernate.show_sql", "true");
		prop.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		return prop;
	}

	@Bean(name="dataSource")
    public BasicDataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        String url ="jdbc:mysql://"+mysqlHost+":"+mysqlPort+"/"+ mysqlDbname +"?characterEncoding=utf-8";
        ds.setUrl(url);
        ds.setUsername(mysqlUser);
        ds.setPassword(mysqlPassword);
        return ds;
    }

	@Bean
	HibernateTransactionManager txManager() {
		return new HibernateTransactionManager(sessionFactory());
	}

	// /////////////////////////////////////////////////////////////////////////////
	//
	// Solr
	//
	// /////////////////////////////////////////////////////////////////////////////

	// @Bean
	// public SolrServer solrServer() {
	// return new HttpSolrServer("http://db.sidooo.com/solr");
	// }

}
