package org.ytoh.configurations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.ytoh.configurations.ui.PropertyRenderer;

/**
 * Marks an annotation as being a property renderer.
 *
 * @author ytoh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Renderer {

    /**
     * {@link PropertyRenderer} implementation to be used.
     *
     * @return <code>PropertyRenderer</code> to be used to get the editing component
     */
    Class<? extends PropertyRenderer> component();
}
