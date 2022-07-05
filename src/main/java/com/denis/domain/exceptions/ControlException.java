package com.denis.domain.exceptions;

// TODO: 7/5/22 how to log in exception
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
