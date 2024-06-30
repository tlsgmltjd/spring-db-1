package com.example.springdb1.test;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class NotDuplicatedEnumValidator implements ConstraintValidator<NotDuplicatedEnum, Object> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(NotDuplicatedEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Set<Enum<?>> enumSet = new HashSet<>();
        boolean isValid = true;

        try {
            for (java.lang.reflect.Field field : value.getClass().getDeclaredFields()) {
                if (field.getType().isEnum() && field.getType().equals(enumClass)) {
                    field.setAccessible(true);
                    Enum<?> enumValue = (Enum<?>) field.get(value);
                    if (enumValue != null && !enumSet.add(enumValue)) {
                        isValid = false;
                        break;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            isValid = false;
        }

        return isValid;
    }
}
