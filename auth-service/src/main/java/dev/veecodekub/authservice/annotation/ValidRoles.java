package dev.veecodekub.authservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.veecodekub.authservice.validation.RolesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RolesValidator.class)
public @interface ValidRoles {
    String message() default "Invalid role. Allowed roles: USER, ADMIN, MANAGER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
