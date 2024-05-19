package com.scottishpower.smartmeter.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumValidator.class)
@Documented
public @interface ValidValueOfEnum {
    Class<? extends Enum<?>> enumClass();
    String field();
    String message() default "Invalid value for field '{field}'. Accepted values are: {acceptedValues}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
