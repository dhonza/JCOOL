package org.ytoh.configurations.test;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.ytoh.configurations.ConfigurationException;

/**
 *
 * @author ytoh
 */
public class MaxLengthValidator implements ConstraintValidator<MaxLength, String> {

    private int maxLength = Integer.MAX_VALUE;

    public void initialize(MaxLength annotation) {
        if (annotation.value() <= 0) {
            throw new ConfigurationException("Max length cannot be less then 1 (was " + annotation.value() + ")");
        }

        maxLength = annotation.value();
    }

    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        if (input == null) {
            return true;
        }

        return input.length() <= maxLength;
    }
}
