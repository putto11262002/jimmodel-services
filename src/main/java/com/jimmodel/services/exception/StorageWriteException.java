package com.jimmodel.services.exception;

public class StorageWriteException extends RuntimeException{
    public StorageWriteException(String message){
        super(message);
    }
}
