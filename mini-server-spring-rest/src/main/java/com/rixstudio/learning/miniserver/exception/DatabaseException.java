package com.rixstudio.learning.miniserver.exception;

public class DatabaseException extends Exception {
    private static final long serialVersionUID = 1189830934048600489L;

    public DatabaseException() {
    }

    public DatabaseException(String message) {
        super(message);
    }
}
