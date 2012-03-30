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
@Constraint(validatedBy = DynamicInRangeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DynamicRange {

    double defaultFrom();

    double defaultTo();

    String fromKey();

    String toKey();

    Class<?>[] groups() default {};

    String message() default "property not in range <{from},{to}>";
}

