package com.jimmodel.internalServices.exception;

public class ConstraintViolationException extends RuntimeException{
    public ConstraintViolationException(String message){
        super(message);
    }
}