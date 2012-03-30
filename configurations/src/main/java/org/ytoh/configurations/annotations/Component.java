package org.ytoh.configurations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Markes a class as being a component.
 *
 * @author ytoh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

    /**
     * Human readable name of this component.
     *
     * @return string name
     */
    String name() default "";

    /**
     * Human readable description of this component.
     *
     * @return string description
     */
    String description() default "";

    /**
     * Human readable short description of this component.
     *
     * @return string short description
     */
    String shortDescription() default "";
}
