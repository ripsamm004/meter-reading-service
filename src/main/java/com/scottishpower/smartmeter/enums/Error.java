package com.scottishpower.smartmeter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum Error {

    // DEFAULTS ERRORS
    INTERNAL_ERROR("C0001", "We are experiencing some issues, Please try later"),
    UNAUTHORIZED_EXCEPTION("C0002", "Unauthorized exception"),
    FORBIDDEN_EXCEPTION("C0003", "Forbidden exception"),
    METHOD_NOT_ALLOWED("C0004", "Method not allowed"),
    BAD_REQUEST("C0005", "Bad request"),
    DUPLICATE_RESOURCE("C0006", "Duplicate resource found"),

    // API ERRORS
    API_ERROR_INVALID_REQUEST("API001", "Invalid request body"),
    API_ERROR_ACCOUNT_NOT_FOUND("API002", "Account not found"),
    API_ERROR_ACCOUNT_ALREADY_EXIST("API003", "Account already exists"),
    API_ERROR_INVALID_METER_READING_LOWER("API004", "Meter reading must be higher than the previous reading"),
    API_ERROR_INVALID_METER_READING_DUPLICATE("API005", "Duplicate meter reading for the given date."),
    API_INVALID_METER_READING_PAST_DATE("API006", "Meter reading date cannot be past then previously submitted reading date."),
    API_ERROR_INVALID_METER_READING_EMPTY("API007", "Meter read requests cannot be empty")
    ;

    private final String code;
    private final String message;
}
