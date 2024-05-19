package com.scottishpower.smartmeter.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValueOfEnumValidator implements ConstraintValidator<ValidValueOfEnum, CharSequence> {
    private List<String> acceptedValues;
    private String fieldName;

    @Override
    public void initialize(ValidValueOfEnum annotation) {
        acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toList());

        fieldName = annotation.field();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = acceptedValues.contains(value.toString());

        if (!isValid) {
            String formattedMessage = context.getDefaultConstraintMessageTemplate()
                .replace("{field}", fieldName)
                .replace("{acceptedValues}", String.join(", ", acceptedValues));

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(formattedMessage)
                .addConstraintViolation();
        }

        return isValid;
    }
}
