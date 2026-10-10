package com.sms.exception;

/**
 * Custom runtime database exception wrapping underlying checked SQLExceptions.
 * Provides user-friendly error messages without leaking raw database stack traces.
 * 
 * // [OOP] Custom Exception Handling: Wraps SQLException with clean abstraction
 */
public class DatabaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
