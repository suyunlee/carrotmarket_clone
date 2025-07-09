// src/main/java/oreumi/group2/carrotClone/validation/UserValidation.java
package oreumi.group2.carrotClone.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.annotation.*;


public class UserValidation {

    @Constraint(validatedBy = UsernameValidator.class)
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidUsername {
        String message() default "아이디는 2~12자의 한글/영문/숫자만 가능합니다.";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class UsernameValidator
            implements ConstraintValidator<ValidUsername, String> {
        // 한글(가-힣), 영문 대소문자, 숫자 2~12자
        private static final String PATTERN = "^[A-Za-z0-9가-힣]{2,12}$";

        @Override
        public boolean isValid(String username, ConstraintValidatorContext context) {
            return username != null && username.matches(PATTERN);
        }
    }

    @Documented
    @Constraint(validatedBy = PasswordConstraintValidator.class)
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidPassword {
        String message() default "비밀번호는 최소 3자리 이상이어야 합니다.";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class PasswordConstraintValidator
            implements ConstraintValidator<ValidPassword, String> {
        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            return password != null && password.length() >= 3;
        }
    }
}
