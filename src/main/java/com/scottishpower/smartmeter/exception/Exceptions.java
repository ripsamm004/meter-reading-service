package com.scottishpower.smartmeter.exception;

import com.scottishpower.smartmeter.enums.Error;

public interface Exceptions {

    BadRequest INVALID_METER_READING_LOWER = new BadRequest(
        Error.API_ERROR_INVALID_METER_READING_LOWER.getMessage(),
        Error.API_ERROR_INVALID_METER_READING_LOWER);

    DuplicateResource INVALID_METER_READING_DUPLICATE = new DuplicateResource(
        Error.API_ERROR_INVALID_METER_READING_DUPLICATE.getMessage(),
        Error.API_ERROR_INVALID_METER_READING_DUPLICATE);

    BadRequest INVALID_METER_READING_EMPTY = new BadRequest(
        Error.API_ERROR_INVALID_METER_READING_EMPTY.getMessage(),
        Error.API_ERROR_INVALID_METER_READING_EMPTY);

    BadRequest INVALID_METER_READING_PAST_DATE = new BadRequest(
        Error.API_INVALID_METER_READING_PAST_DATE.getMessage(),
        Error.API_INVALID_METER_READING_PAST_DATE);

    DuplicateResource API_ERROR_ACCOUNT_ALREADY_EXIST = new DuplicateResource(
        Error.API_ERROR_ACCOUNT_ALREADY_EXIST.getMessage(),
        Error.API_ERROR_ACCOUNT_ALREADY_EXIST);

    NotFound API_ERROR_ACCOUNT_NOT_FOUND = new NotFound(
        Error.API_ERROR_ACCOUNT_NOT_FOUND.getMessage(),
        Error.API_ERROR_ACCOUNT_NOT_FOUND);


}
