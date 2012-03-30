package org.ytoh.configurations.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;

/**
 *
 * @author ytoh
 */
@Constraint(validatedBy = MaxLengthValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MaxLength {

    int value();

    String message() default "Max length exceeded (max: {value})";

    Class<?>[] groups() default {};
}
