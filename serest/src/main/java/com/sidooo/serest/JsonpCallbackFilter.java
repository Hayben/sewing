package com.sidooo.serest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kimzhang
 * Date: 15-1-14
 * Time: 下午4:32
 * To change this template use File | Settings | File Templates.
 */
public class JsonpCallbackFilter implements Filter {

    private static Log log = LogFactory.getLog(JsonpCallbackFilter.class);

    public void init(FilterConfig fConfig) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Map<String, String[]> parms = httpRequest.getParameterMap();

        if(parms.containsKey("callback")) {
            if(log.isDebugEnabled())
                log.debug("Wrapping response with JSONP callback '" + parms.get("callback")[0] + "'");

            OutputStream out = httpResponse.getOutputStream();

            GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);

            chain.doFilter(request, wrapper);

            out.write(new String(parms.get("callback")[0] + "(").getBytes());
            out.write(wrapper.getData());
            out.write(new String(");").getBytes());

            wrapper.setContentType("text/javascript;charset=UTF-8");

            out.close();
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {}
}
