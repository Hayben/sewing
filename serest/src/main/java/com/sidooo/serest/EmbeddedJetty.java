package com.sidooo.serest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class EmbeddedJetty {

	public static void main(String[] args) throws Exception {
		new EmbeddedJetty().startJetty(7495);
	}
	
	private void startJetty(int port) throws Exception {
		Server server = new Server(port);
		server.setHandler(getServletContextHandler(getContext()));
		server.start();
		server.join();
	}
	
    private ServletContextHandler getServletContextHandler(WebApplicationContext context) throws Exception {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath("/");
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), "/*");
        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase(new ClassPathResource("webapp").getURI().toString());
        return contextHandler;
    }

    private WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("com.sidooo.config");
        context.getEnvironment().setDefaultProfiles("dev");
        return context;
    }
}
