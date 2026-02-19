package dev.veecodekub.authservice.validation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import dev.veecodekub.authservice.annotation.ValidRoles;
import dev.veecodekub.authservice.enumerate.Roles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RolesValidator implements ConstraintValidator<ValidRoles, String> {

    Set<String> acceptedValues;

    @Override
    public void initialize(ValidRoles constraintAnnotation) {
        acceptedValues = Arrays.stream(Roles.values())
                .map(Roles::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return acceptedValues.contains(value);
    }
}