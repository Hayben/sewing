package com.sidooo.config.core;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.sidooo.config.AppConfig;

/**
 * Created with IntelliJ IDEA.
 * User: kimzhang
 * Date: 15-4-26
 * Time: 下午7:55
 * To change this template use File | Settings | File Templates.
 */
public class SpringMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}
