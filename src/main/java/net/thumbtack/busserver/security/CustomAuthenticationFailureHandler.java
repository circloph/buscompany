package net.thumbtack.busserver.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import net.thumbtack.busserver.error.Error;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.ErrorValidation;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    MappingJackson2HttpMessageConverter httpMessageConverter;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(400); 
        ErrorValidation error = new ErrorValidation();
        error.getAllErrors().add(new Error(ErrorCode.USER_NOT_EXIST.toString(), "login, password", "invalid login or password"));
        HttpOutputMessage outputMessage = new ServletServerHttpResponse(response);
        httpMessageConverter.write(error.getAllErrors(), MediaType.APPLICATION_JSON, outputMessage);
    }
    
}
