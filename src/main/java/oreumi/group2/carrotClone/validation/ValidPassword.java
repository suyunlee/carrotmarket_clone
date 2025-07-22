package oreumi.group2.carrotClone.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @ValidPassword
 * 최소 8자 이상
 * 영문,숫자, 특수문자(!@#$%) 각각 최소 1회 이상 포함
 */

@Documented
@Constraint(validatedBy = PasswordConstrainValidaor.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "비밀번호는 최소 8자 이상, 영문, 숫자, 특수문자를 모두 포함해야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
