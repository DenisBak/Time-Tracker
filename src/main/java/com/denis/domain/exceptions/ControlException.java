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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Throwable cause = getCause();
        result.append(super.toString());

        while (cause != null) {
            result.append("\n\t ").append(cause);
            cause = cause.getCause();
        }
        return result.toString();
    }
}
