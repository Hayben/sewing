package com.sidooo.senode;

import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.sidooo.division"})
@PropertySource(value = "classpath:mysql.properties")
public class MysqlConfiguration {
	
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
}
