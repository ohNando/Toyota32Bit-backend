package com.toyota.toyotabackend.restapi.exception;

/**
 * Exception thrown when an invalid command is encountered in the system.
 */
public class CommandNotValidException extends RuntimeException {

    /**
     * Constructs a new CommandNotValidException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method).
     */
    public CommandNotValidException(String message) {
        super(message);
    }
}
