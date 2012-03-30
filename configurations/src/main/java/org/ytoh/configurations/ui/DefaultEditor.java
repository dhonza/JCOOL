package org.ytoh.configurations.ui;

import java.awt.Component;
import java.lang.annotation.Annotation;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.context.PublishingContext;

/**
 * Default {@link PropertyEditor} implementation.
 *
 * @author ytoh
 */
public class DefaultEditor implements PropertyEditor {

    public Component getEditorComponent(Property property, Annotation annotation, PublishingContext context) {
        return new DefaultRenderer().getRendererComponent(property, annotation);
    }
}
