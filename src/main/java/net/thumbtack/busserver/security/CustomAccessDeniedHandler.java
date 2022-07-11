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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import net.thumbtack.busserver.error.Error;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.ErrorValidation;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    MappingJackson2HttpMessageConverter httpMessageConverter;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
    AccessDeniedException accessDeniedException) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            ErrorValidation error = new ErrorValidation();
            error.getAllErrors().add(new Error(ErrorCode.ACCESS_DENIED.toString(), "", "access denied"));
            HttpOutputMessage outputMessage = new ServletServerHttpResponse(response);
            httpMessageConverter.write(error.getAllErrors(), MediaType.APPLICATION_JSON, outputMessage);
    }
    
}
