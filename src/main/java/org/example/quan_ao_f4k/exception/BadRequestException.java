package org.example.quan_ao_f4k.exception;

import org.example.quan_ao_f4k.util.F4KConstants;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Object ...params) {
        super(String.format(message, params));
    }

    public BadRequestException(F4KConstants.ErrCode errCode, Object ...params) {
        super(String.format(errCode.getDescription(), params));
    }
}