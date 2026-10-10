package com.sms.exception;

/**
 * Thrown when attempting to register or update an entity with an email address
 * that already exists in the system.
 * 
 * // [OOP] Custom Exception Handling
 */
public class DuplicateEmailException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
