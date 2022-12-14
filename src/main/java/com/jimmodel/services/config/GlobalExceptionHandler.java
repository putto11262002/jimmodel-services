package com.jimmodel.services.config;

import com.jimmodel.services.dto.Response.ErrorResponse;
import com.jimmodel.services.exception.ConstraintViolationException;
import com.jimmodel.services.exception.JwtException;
import com.jimmodel.services.exception.ResourceNotFoundException;
import com.jimmodel.services.exception.ValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(HttpServletRequest request, Exception exception){

        exception.printStackTrace(); // for development
        System.out.println(exception);

        ErrorResponse responseBody = ErrorResponse.builder()
                .messages("Something went wrong. Please try again later.")
                .errorMessageKey(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .timestamp(Instant.now())
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .url(request.getRequestURI())
                .method(request.getMethod())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundExceptionHandler(HttpServletRequest request, ResourceNotFoundException exception){
        ErrorResponse responseBody = ErrorResponse.builder()
                .messages(exception.getMessage())
                .errorMessageKey(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .url(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> validationException(HttpServletRequest request, ValidationException exception){
        ErrorResponse responseBody = ErrorResponse.builder()
                .messages(exception.getMessage())
                .errorMessageKey(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .url(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> ExpiredJwtException(HttpServletRequest request){
        ErrorResponse responseBody = ErrorResponse.builder()
                .messages("Token is expired.")
                .errorMessageKey(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .errorCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(Instant.now())
                .url(request.getRequestURI())
                .method(request.getMethod())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> JwtExceptions(HttpServletRequest request, Exception e){
        ErrorResponse responseBody = ErrorResponse.builder()
                .messages(e.getMessage())
                .errorMessageKey(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .errorCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(Instant.now())
                .url(request.getRequestURI())
                .method(request.getMethod())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> InsufficientAuthenticationException(HttpServletRequest request, Exception e){

        ErrorResponse responseBody = ErrorResponse.builder()
                .messages(e.getMessage())
                .errorMessageKey(HttpStatus.FORBIDDEN.getReasonPhrase())
                .errorCode(HttpStatus.FORBIDDEN.value())
                .timestamp(Instant.now())
                .url(request.getRequestURI())
                .method(request.getMethod())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> AccessDeniedException(HttpServletRequest request, Exception e){
        ErrorResponse responseBody = ErrorResponse.builder()
                .messages(e.getMessage())
                .errorMessageKey(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .errorCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(Instant.now())
                .url(request.getRequestURI())
                .method(request.getMethod())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> ConstraintViolationException(HttpServletRequest request, Exception e){
        ErrorResponse responseBody = ErrorResponse.builder()
                .messages(e.getMessage())
                .errorMessageKey(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .url(request.getRequestURI())
                .method(request.getMethod())
                .build();
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

}