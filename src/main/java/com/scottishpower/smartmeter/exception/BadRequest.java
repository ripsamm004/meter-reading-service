package com.scottishpower.smartmeter.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.scottishpower.smartmeter.enums.Error;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequest extends BaseHTTPException {
    private static final long serialVersionUID = 2618038155811062635L;

    public BadRequest(String logMessage, Error error) {
        super(logMessage, error);
    }
}
