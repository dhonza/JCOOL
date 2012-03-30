package org.ytoh.configurations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.ytoh.configurations.ui.PropertyEditor;

/**
 * Marks an annotation as being a property editor.
 *
 * @author ytoh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Editor {

    /**
     * {@link PropertyEditor} implementation to be used.
     *
     * @return <code>PropertyEditor</code> to be used to get the editing component
     */
    Class<? extends PropertyEditor> component();
}
