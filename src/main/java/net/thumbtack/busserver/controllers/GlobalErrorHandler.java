package net.thumbtack.busserver.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import net.thumbtack.busserver.error.Error;
import net.thumbtack.busserver.error.ErrorCode;
import net.thumbtack.busserver.exception.BusCompanyException;
import net.thumbtack.busserver.exception.ErrorValidation;
import net.thumbtack.busserver.exception.ServerException;
@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(BusCompanyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorValidation handleValidation(BusCompanyException exc) {
        ErrorValidation errorValidation = new ErrorValidation();
        errorValidation.getAllErrors().add(exc.getError());
        return errorValidation;
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorValidation handleValidation(ServerException exc) {
        ErrorValidation errorValidation = new ErrorValidation();
        errorValidation.getAllErrors().add(exc.getError());
        return errorValidation;
    } 

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorValidation handleValidation(MethodArgumentNotValidException exc) {
        ErrorValidation errorValidation = new ErrorValidation();
        exc.getBindingResult().getFieldErrors().forEach(fieldError ->
        errorValidation.getAllErrors().add(new Error(
            ErrorCode.valueOf(fieldError.getDefaultMessage()).toString(), 
            fieldError.getField(), 
            ErrorCode.valueOf(fieldError.getDefaultMessage()).getMessage())));

            exc.getBindingResult().getGlobalErrors().forEach(fieldError ->
        errorValidation.getAllErrors().add(new Error(
            ErrorCode.valueOf(fieldError.getDefaultMessage()).toString(), 
            fieldError.getObjectName(), 
            ErrorCode.valueOf(fieldError.getDefaultMessage()).getMessage())));
        return errorValidation;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorValidation handleValidation(Throwable exc) {
        ErrorValidation errorValidation = new ErrorValidation();
        errorValidation.getAllErrors().add(new Error(ErrorCode.SERVER_ERROR.toString(), "", "internal server error"));
        return errorValidation;
    } 

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorValidation handleValidation(HttpMessageNotReadableException e) {
        ErrorValidation errorValidation = new ErrorValidation();
        errorValidation.getAllErrors().add(new Error(ErrorCode.INVALID_JSON.toString(), "", "failed to read json"));
        return errorValidation;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ErrorValidation handleValidation(MissingServletRequestParameterException e) {
        ErrorValidation errorValidation = new ErrorValidation();
        errorValidation.getAllErrors().add(new Error(ErrorCode.MISSING_PARAMETER.toString(), "", "missing parameter"));
        return errorValidation;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ErrorValidation handleValidation(NoHandlerFoundException e) {
        ErrorValidation errorValidation = new ErrorValidation();
        errorValidation.getAllErrors().add(new Error(ErrorCode.NOT_FOUND.toString(), "", "page not found"));
        return errorValidation;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ErrorValidation handleValidation(HttpMediaTypeNotSupportedException e) {
        ErrorValidation errorValidation = new ErrorValidation();
        errorValidation.getAllErrors().add(new Error(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.toString(), "", "content type not supported"));
        return errorValidation;
    }



}
