package org.ytoh.configurations.ui;

import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import java.awt.Component;
import javax.swing.JComboBox;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.context.PublishingContext;

/**
 * A {@link PropertyEditor} instance editing property values using a JComboBox.
 *
 * @author ytoh
 */
public class DropDownEditor implements PropertyEditor<Object, DropDown> {
    private final Object[] overrides;

    public DropDownEditor(Object[] overrides) {
        this.overrides = overrides;
    }

    public DropDownEditor() {
        this(null);
    }

    public Component getEditorComponent(Property<Object> property, DropDown annotation, PublishingContext context) {
        Object[] values;

        if(overrides != null) {
            values = overrides;
        } else {
            values = annotation.value();
        }

        ValueModel model = new PropertyAdapter(property, "value");
        JComboBox box = new JComboBox();
        box.setModel(new ComboBoxAdapter(values, model));
        return box;
    }
}
