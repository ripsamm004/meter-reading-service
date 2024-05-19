package com.scottishpower.smartmeter.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.scottishpower.smartmeter.enums.Error;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerException extends BaseHTTPException {
  private static final long serialVersionUID = 9060843738556641076L;
  public ServerException(String logMessage, Error error) {
    super(logMessage, error);
  }
}
