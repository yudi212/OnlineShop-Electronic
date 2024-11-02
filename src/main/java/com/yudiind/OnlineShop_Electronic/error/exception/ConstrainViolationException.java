package com.yudiind.OnlineShop_Electronic.error.exception;

public class ConstrainViolationException extends RuntimeException{

    public ConstrainViolationException() {
    }

    public ConstrainViolationException(String message) {
        super(message);
    }

    public ConstrainViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstrainViolationException(Throwable cause) {
        super(cause);
    }
}
