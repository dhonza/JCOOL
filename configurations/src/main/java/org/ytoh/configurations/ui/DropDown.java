package org.ytoh.configurations.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.ytoh.configurations.annotations.Editor;

/**
 * Edit as a drop down.
 *
 * @author ytoh
 */
@Editor(component = DropDownEditor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DropDown {

    /**
     * Options to choose from.
     *
     * @return array of string valies to choose from
     */
    String[] value() default {};
}
