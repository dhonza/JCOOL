package org.ytoh.configurations.ui;

import com.jgoodies.binding.adapter.ToggleButtonAdapter;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import java.awt.Component;
import javax.swing.JCheckBox;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.context.PublishingContext;

/**
 * A {@link PropertyEditor} and {@link PropertyRenderer} implementation binding
 * the underlying <code>Boolean</code> value to a JCheckBox.
 *
 * @author ytoh
 */
public class CheckBoxEditor implements PropertyEditor<Boolean, CheckBox>, PropertyRenderer<Boolean, CheckBox> {

    public Component getEditorComponent(Property<Boolean> property, CheckBox annotation, PublishingContext context) {
        ValueModel model = new PropertyAdapter(property, "value", true);
        JCheckBox box = new JCheckBox();
        box.setModel(new ToggleButtonAdapter(model));
        return box;
    }

    public Component getRendererComponent(Property<Boolean> property, CheckBox annotation) {
        ValueModel model = new PropertyAdapter(property, "value", true);
        JCheckBox box = new JCheckBox();
        box.setModel(new ToggleButtonAdapter(model));
        return box;
    }
}
