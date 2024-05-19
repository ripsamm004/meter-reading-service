package com.scottishpower.smartmeter.exception;

import com.scottishpower.smartmeter.enums.Error;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateResource extends BaseHTTPException {
    private static final long serialVersionUID = 2938880255469015184L;

    public DuplicateResource(String logMessage, Error error) {
        super(logMessage, error);
    }
}
