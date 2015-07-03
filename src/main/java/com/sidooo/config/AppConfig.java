package com.sidooo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.sidooo.senode.DatawareConfiguration;
import com.sidooo.senode.MongoConfiguration;


@Configuration
@EnableWebMvc
//@ComponentScan({"com.sidooo.*"})
@Import({ MongoConfiguration.class })
public class AppConfig {

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }
    
//    @Bean
//    public InternalResourceViewResolver viewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setViewClass();
//        viewResolver.setPrefix("/WEB-INF/pages/");
//        viewResolver.setSuffix(".jsp");
//        return viewResolver;
//    }
//    
//	@Bean
//	public SessionFactory mongodbFactory() {
//		
//		OgmConfiguration cfgogm = new OgmConfiguration();
//		
//        cfgogm.setProperty(Environment.TRANSACTION_STRATEGY,
//        		"org.hibernate.transaction.JTATransactionFactory");
//		
//        cfgogm.setProperty(Environment.JTA_PLATFORM, 
//        		"org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform");
//        // configure MongoDB connection
//        cfgogm.setProperty("hibernate.ogm.datastore.provider","mongodb");
//        cfgogm.setProperty("hibernate.ogm.datastore.grid_dialect",
//                "org.hibernate.ogm.dialect.mongodb.MongoDBDialect");
//        cfgogm.setProperty("hibernate.ogm.mongodb.database", "ontology");
//        cfgogm.setProperty("hibernate.ogm.mongodb.host", "db.sidooo.com");
//        cfgogm.setProperty("hibernate.ogm.mongodb.port", "27017");
//        cfgogm.addAnnotatedClass(Ontology.class)
//		   .addAnnotatedClass(Member.class)
//		   .addAnnotatedClass(Category.class);
//        
//        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
//        builder.applySettings(cfgogm.getProperties());
//        
//        return cfgogm.buildSessionFactory(builder.buildServiceRegistry());
//	}
    



}
