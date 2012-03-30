package org.ytoh.configurations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.ytoh.configurations.annotations.Range;

/**
 * A {@link ConstraintValidator} instance validating numbers for a certain range.
 *
 * @author ytoh
 */
public class InRangeValidator implements ConstraintValidator<Range, Number> {
    private double low = Double.MIN_VALUE;
    private double high = Double.MAX_VALUE;

    public void initialize(Range annotation) {
        if(annotation.from() >= annotation.to()) {
            throw new ConfigurationException("Invalid range ("+ annotation.from() + " -> " + annotation.to() + "). Property cannot be set.");
        }

        low = annotation.from();
        high = annotation.to();
    }

    public boolean isValid(Number input, ConstraintValidatorContext constraintValidatorContext) {
        if(input == null) {
            return true;
        }

        return input.doubleValue() >= low && input.doubleValue() <= high;
    }
}
