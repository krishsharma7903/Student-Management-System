package com.sms.exception;

/**
 * Thrown when marks supplied to the system fall outside the valid domain (0 to 100)
 * or when individual assessment components exceed allowed maximum limits.
 * 
 * // [OOP] Custom Exception Handling
 */
public class InvalidMarksException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidMarksException(String message) {
        super(message);
    }

    public InvalidMarksException(String message, Throwable cause) {
        super(message, cause);
    }
}
