package com.mendohard.api.exception;


public class IntentosMaximosException extends RuntimeException {

    public IntentosMaximosException(String message) {
        super(message);
    }

    public IntentosMaximosException(String message, Throwable cause) {
        super(message, cause);
    }
}