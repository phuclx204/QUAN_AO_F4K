package org.example.quan_ao_f4k.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Object ...params) {
        super(String.format(message, params));
    }
}