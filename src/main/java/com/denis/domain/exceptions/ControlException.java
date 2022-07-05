package com.denis.domain.exceptions;

public class ControlException extends Exception{
    public ControlException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControlException() {
    }

    public ControlException(String message) {
        super(message);
    }

    public ControlException(Throwable throwable) {
        super(throwable);
    }
}
