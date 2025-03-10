package com.toyota.toyotabackend.restapi.exception;

public class CommandNotValidException extends RuntimeException {
    public CommandNotValidException(String message) {
        super(message);
    }
}
