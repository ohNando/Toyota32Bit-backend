package com.toyota.toyotabackend.restapi.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {super(message);}
}
