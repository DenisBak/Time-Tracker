package com.denis.domain.exceptions;

public class DAOException extends Exception{
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(Throwable throwable) {
        super(throwable);
    }

    public DAOException(String message) {

    }
}
