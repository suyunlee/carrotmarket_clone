package oreumi.group2.carrotClone.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @ValidUsername
 * 이메일 형식 (@) 포함 만 허용
 */
@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface ValidUserrname {
    String message() default "@를 포함한 유효한 이메일 주소를 입력 해주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
