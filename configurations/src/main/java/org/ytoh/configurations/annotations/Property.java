package org.ytoh.configurations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link java.lang.reflect.Field} as being a property.
 *
 * @author ytoh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

    /**
     * Human readable name of this property.
     *
     * @return a human readable name of this property
     */
    String name() default "";

    /**
     * Human readable description of this property.
     *
     * @return string description
     */
    String description() default "";
}
