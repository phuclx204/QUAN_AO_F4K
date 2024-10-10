package org.example.quan_ao_f4k.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, String ...params) {
        super(String.format(message, (Object[]) params));
    }
}