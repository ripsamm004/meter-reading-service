package com.scottishpower.smartmeter.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.scottishpower.smartmeter.enums.MeterReadType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

class ValueOfEnumValidatorTest {

    private Validator validator;

    static class TestData {
        @ValidValueOfEnum(enumClass = MeterReadType.class, field = "type", message = "Invalid type")
        private String type;
    }

    private TestData testData;

    @BeforeEach
    void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        testData = new TestData();
    }

    @Test
    void valueOfEnumValidator_whenNull_thenReturnTrue() {
        Set<ConstraintViolation<TestData>> violations = getValidate();
        assertTrue(violations.isEmpty());
    }

    @Test
    void valueOfEnumValidator_whenInvalidValue_thenReturnFalse() {
        testData.type = "SOLAR";
        Set<ConstraintViolation<TestData>> violations = getValidate();
        assertThat(violations).anyMatch(havingPropertyPath("type").and(havingMessage("Invalid type")));
    }

    @Test
    void valueOfEnumValidator_whenValidValue_thenReturnTrue() {
        testData.type = MeterReadType.GAS.name();
        Set<ConstraintViolation<TestData>> violations = getValidate();
        assertTrue(violations.isEmpty());

        testData.type = MeterReadType.ELEC.name();
        violations = getValidate();
        assertTrue(violations.isEmpty());
    }

    private Set<ConstraintViolation<TestData>> getValidate() {
        return validator.validate(testData);
    }

    private Predicate<ConstraintViolation<TestData>> havingMessage(String message) {
        return l -> message.equals(l.getMessage());
    }

    private Predicate<ConstraintViolation<TestData>> havingPropertyPath(String propertyPath) {
        return l -> propertyPath.equals(l.getPropertyPath().toString());
    }
}
