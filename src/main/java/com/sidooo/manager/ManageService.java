package com.sidooo.manager;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ManageService {
	
	public static void main(String[] args) throws Exception {
		new ManageService().startJetty(4151);
	}
	
	private void startJetty(int port) throws Exception {
		Server server = new Server(port);
		server.setHandler(getServletContextHandler(getContext()));
		server.start();
		server.join();
	}
	
    private Handler getServletContextHandler(WebApplicationContext context) throws Exception {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath("/");
        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase(new ClassPathResource("webapp").getURI().toString());
        
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), "/*");
//        contextHandler.addServlet(new ServletHolder(new DefaultServlet()), "/wface/*");
        
        FilterHolder cors = contextHandler.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        return contextHandler;
    }
    
//    private Handler getResourceHandler(WebApplicationContext content) throws IOException {
//    	ResourceHandler handler = new ResourceHandler();
//    	handler.setDirectoriesListed(true);
//    	handler.setWelcomeFiles(new String[]{"index.html"});
//    	handler.setResourceBase(new ClassPathResource("webapp").getURI().toString());
//    	return handler;
//    }

    private WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = 
        		new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("com.sidooo.manager");
        context.getEnvironment().setDefaultProfiles("dev");
        return context;
    }
}
