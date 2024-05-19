package com.scottishpower.smartmeter.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.scottishpower.smartmeter.enums.Error;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {
        List<String> errorList = ex.getBindingResult().getAllErrors().stream()
            .map(ObjectError::getDefaultMessage)
            .toList();
        String result = errorList.get(0);
        ErrorDetails errorDetails = new ErrorDetails(Error.BAD_REQUEST.getCode(), result);
        log.error("MethodArgumentNotValidException", ex);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        org.springframework.web.HttpRequestMethodNotSupportedException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {
        log.error("MethodNotAllowedException", ex);
        ErrorDetails errorDetails = new ErrorDetails(Error.METHOD_NOT_ALLOWED.getCode(),
            Error.METHOD_NOT_ALLOWED.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
        ConstraintViolationException ex, WebRequest request) {
        List<String> errorList = ex.getConstraintViolations().stream()
            .map(violation -> violation.getMessage())
            .toList();
        String result = errorList.get(0);
        ErrorDetails errorDetails = new ErrorDetails(Error.BAD_REQUEST.getCode(),
            result);
        log.error("ConstraintViolationException", ex);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<Object> handleNotFoundException(NotFound ex, WebRequest request) {
        log.error("NotFoundException", ex);
        return response(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResource.class)
    public ResponseEntity<Object> duplicateResourceException(DuplicateResource ex,
                                                             WebRequest request) {
        log.error("DuplicateResourceException", ex);
        return response(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequest ex,
                                                            WebRequest request) {
        log.error("BadRequestException", ex);
        return response(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleGenericThrowable(Throwable ex, WebRequest request) {
        log.error("BadRequestException", ex);
        ErrorDetails errorDetails = new ErrorDetails(Error.INTERNAL_ERROR.getCode(),
                Error.INTERNAL_ERROR.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<Object> response(BaseHTTPException ex, HttpStatus httpStatus) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getApiError().getCode(),
                ex.getApiError().getMessage());
        return new ResponseEntity<>(errorDetails, httpStatus);
    }
}
