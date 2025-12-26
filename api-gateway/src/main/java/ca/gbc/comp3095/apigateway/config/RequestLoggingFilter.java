package ca.gbc.comp3095.apigateway.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    /**
     * Logs incoming HTTP requests (method, URI, remote address)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        log.info("Incoming request: Method = {}, URI = {}, Remote Address = {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());

        filterChain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
        log.info("Destroying RequestLoggingFilter...");
    }
}