package com.sidooo.manager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.sidooo.senode.MongoConfiguration;
import com.sidooo.senode.MysqlConfiguration;

@Configuration
@EnableWebMvc
@ComponentScan({ "com.sidooo.manager", "com.sidooo.division",
		"com.sidooo.seed", "com.sidooo.point" })
@Import({ MongoConfiguration.class, MysqlConfiguration.class })
public class ManageConfiguration {

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

}
