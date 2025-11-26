package org.example.remedy.domain.dropping.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DroppingTypeValidator.class)
public @interface ValidDroppingType {
    String message() default "드랍 타입에 맞지 않는 필드가 포함되어 있습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}