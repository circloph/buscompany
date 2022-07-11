package net.thumbtack.busserver.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class CustomAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${user_idle_timeout}")
    private int userIdleTimeout;

    public CustomAuthenticationSuccessHandler() {
        super();
        setUseReferer(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse,
    Authentication authentication) throws IOException, ServletException {
    setDefaultTargetUrl("http://localhost:8080"+httpServletRequest.getRequestURI());
    super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
    }
}
