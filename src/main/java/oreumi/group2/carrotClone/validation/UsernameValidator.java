package oreumi.group2.carrotClone.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUserrname, String> {
    private static final String PATTERN =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        return value != null && value.matches(PATTERN);
    }

}
