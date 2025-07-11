package oreumi.group2.carrotClone.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstrainValidaor
        implements ConstraintValidator<ValidPassword, String> {
    private static final String PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$";
    // (?=.*[A-Za-z]) : 영문자 최소 1회
    // (?=.*\\d)     : 숫자 최소 1회
    // (?=.*[@$!%*#?&]) : 특수문자 최소 1회
    // .{8,}         : 전체 최소길이 8자 이상

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        return value != null && value.matches(PATTERN);
    }
}
