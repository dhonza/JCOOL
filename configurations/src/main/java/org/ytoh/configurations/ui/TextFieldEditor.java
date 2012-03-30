package org.ytoh.configurations.ui;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTextField;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.context.PublishingContext;

/**
 * A {@link PropertyEditor} implementation binding the underlying {@link Property} value to a JTextField.
 *
 * @author ytoh
 */
public class TextFieldEditor implements PropertyEditor<Object, TextField> {

    public Component getEditorComponent(final Property<Object> property, TextField annotation, PublishingContext context) {
        final ValueModel model = new PropertyAdapter(property, "value", true);
        final ValueModel stateModel = new PropertyAdapter(property, "propertyState", true);
        final JTextField field = BasicComponentFactory.createTextField(model, true);

        stateModel.addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                field.setEnabled(((PropertyState)evt.getNewValue()) == PropertyState.ENABLED);
            }
        });

        return field;
    }
}
