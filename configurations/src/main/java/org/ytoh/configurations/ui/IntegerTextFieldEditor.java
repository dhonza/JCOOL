package org.ytoh.configurations.ui;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import java.awt.Component;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.context.PublishingContext;

/**
 * A {@link PropertyEditor} implementation editing the number property as an integer.
 *
 * @author ytoh
 */
public class IntegerTextFieldEditor implements PropertyEditor<Number, TextField> {

    public Component getEditorComponent(final Property<Number> property, TextField annotation, PublishingContext context) {
        final ValueModel model = new PropertyAdapter(property, "value", true);
        return BasicComponentFactory.createIntegerField(model);
    }
}