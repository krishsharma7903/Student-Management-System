package com.sms.exception;

/**
 * Thrown when a requested student cannot be found in the database.
 * 
 * // [OOP] Custom Exception Handling
 */
public class StudentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
