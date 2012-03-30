package org.ytoh.configurations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.ytoh.configurations.annotations.OneOf;

/**
 * A {@link ConstraintValidator} instance validating <code>String</code>s.
 *
 * @author ytoh
 */
public class OneOfValidator implements ConstraintValidator<OneOf, String> {

    private String[] options = new String[0];

    public void initialize(OneOf annotation) {
        if (annotation.value().length == 0) {
            throw new ConfigurationException("No options defined. (The propery cannot be set.)");
        }

        options = annotation.value();
    }

    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        if (input == null) {
            return true;
        }

        for (String option : options) {
            if (option.equals(input)) {
                return true;
            }
        }

        return false;
    }
}
