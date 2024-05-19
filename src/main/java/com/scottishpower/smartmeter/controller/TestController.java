package com.scottishpower.smartmeter.controller;

import com.scottishpower.smartmeter.exception.BadRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.scottishpower.smartmeter.enums.Error;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import com.scottishpower.smartmeter.exception.DuplicateResource;

@RestController
@RequestMapping("/test")
@Validated
public class TestController {

    @GetMapping("/bad-request")
    public void throwBadRequest() {
        throw new BadRequest("Bad request", Error.BAD_REQUEST);
    }

    @GetMapping("/duplicate-resource")
    public void throwDuplicateResource() {
        throw new DuplicateResource("Duplicate resource", Error.DUPLICATE_RESOURCE);
    }

    @GetMapping("/internal-error")
    public void throwInternalError() {
        throw new RuntimeException("Internal error");
    }

    @PostMapping("/method-argument-not-valid")
    public void methodArgumentNotValid(@Valid @RequestBody TestRequest request) {
        // This method will trigger MethodArgumentNotValidException if the request is invalid
    }

    @PatchMapping("/method-not-allowed")
    public void methodNotAllowed() {
        // This method is just to trigger MethodNotAllowedException when accessed with an unsupported HTTP method
    }

    static class TestRequest {
        @NotEmpty(message = "Field cannot be empty")
        private String field;

        // Getters and Setters
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
