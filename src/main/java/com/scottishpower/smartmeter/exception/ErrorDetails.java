package com.scottishpower.smartmeter.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDetails {
    private String code;
    private String message;
}
