package org.ytoh.configurations.test;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.ytoh.configurations.context.ContextAware;
import org.ytoh.configurations.context.MutableContext;

/**
 *
 * @author ytoh
 */
public class DynamicInRangeValidator implements ConstraintValidator<DynamicRange,Number>, ContextAware {
    private DynamicRange range;
    private MutableContext context;

    public void initialize(DynamicRange constraintAnnotation) {
        this.range = constraintAnnotation;
    }

    public void registerContext(MutableContext context) {
        this.context = context;
    }

    public String getErrorMessage(double low, double high) {
        return "Property not in range <" + low + "," + high + ">";
    }

    public boolean isValid(Number input, ConstraintValidatorContext constraintValidatorContext) {
        if(input == null) {
            return true;
        }

        double low = range.defaultFrom();
        double high = range.defaultTo();

        if(context != null) {

            Double contextLow = context.get(Double.class, range.fromKey());
            Double contextHigh = context.get(Double.class, range.toKey());

            if(contextLow != null) {
                low = contextLow;
            }

            if(contextHigh != null) {
                high = contextHigh;
            }

            if(!(input.doubleValue() >= low && input.doubleValue() <= high)) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(getErrorMessage(low, high));
                return false;
            }
        }

        return input.doubleValue() >= low && input.doubleValue() <= high;
    }
}
