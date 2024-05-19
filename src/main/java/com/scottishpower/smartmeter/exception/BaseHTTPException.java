package com.scottishpower.smartmeter.exception;

import lombok.Getter;
import com.scottishpower.smartmeter.enums.Error;

@Getter
public abstract class BaseHTTPException extends RuntimeException {

    private static final long serialVersionUID = 3931380418074293785L;

    private final Error apiError;

    protected BaseHTTPException() {
        super();
        this.apiError = null;
    }

    protected BaseHTTPException(String logMsg, Error error) {
        super(logMsg);
        this.apiError = error;
    }
}
