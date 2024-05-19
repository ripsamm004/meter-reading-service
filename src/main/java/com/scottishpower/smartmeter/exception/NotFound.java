package com.scottishpower.smartmeter.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.scottishpower.smartmeter.enums.Error;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFound extends BaseHTTPException {
  private static final long serialVersionUID = -4987804324914642431L;
  public NotFound(String logMessage, Error error) {
    super(logMessage, error);
  }
}
