package com.sidooo.manager;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.sidooo.senode.MongoConfiguration;

@Configuration
@EnableWebMvc
@ComponentScan({"com.sidooo.manager"})
@Import({ MongoConfiguration.class })
public class ManageConfiguration {

}
