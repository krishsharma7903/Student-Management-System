package com.sms.exception;

/**
 * Thrown when credentials fail authentication or an unauthorized user
 * attempts to execute privileged operations.
 * 
 * // [OOP] Custom Exception Handling
 */
public class AuthenticationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
