package com.mendohard.api.exception;


import java.util.List;

public class IntentosMaximosException extends RuntimeException {

    public IntentosMaximosException(String message) {
        super(message);
    }

    public IntentosMaximosException(String message, Throwable cause) {
        super(message, cause);
    }
}