package br.com.desafio.exception;

public class ErrorToAccessDatabaseException extends RuntimeException {
    public ErrorToAccessDatabaseException(String message) {
        super(message);
    }
}

