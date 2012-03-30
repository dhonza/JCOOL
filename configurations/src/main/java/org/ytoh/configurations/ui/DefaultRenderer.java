package org.ytoh.configurations.ui;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.ytoh.configurations.Property;

/**
 * A default {@link PropertyRenderer} implementation displaying the property value
 * in a JLabel.
 *
 * @author ytoh
 */
public class DefaultRenderer implements PropertyRenderer {

    public Component getRendererComponent(final Property property, Annotation annotation) {
        final ValueModel model = new PropertyAdapter(property, "value", true);
        final JPanel panel = new JPanel(new BorderLayout());
        final JLabel label = new JLabel(String.valueOf(property.getValue()));

        model.addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                label.setText(String.valueOf(property.getValue()));
            }
        });

        panel.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("foreground".equals(evt.getPropertyName())) {
                    label.setForeground((Color) evt.getNewValue());
                } else if ("background".equals(evt.getPropertyName())) {
                    label.setBackground((Color) evt.getNewValue());
                }
            }
        });

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
