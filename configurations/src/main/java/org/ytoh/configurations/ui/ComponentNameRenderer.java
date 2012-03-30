/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
 * A {@link PropertyRenderer} instance displaying the component's human readable
 * name.
 *
 * @author ytoh
 */
public class ComponentNameRenderer implements PropertyRenderer<Object, Annotation> {

    public Component getRendererComponent(final Property<Object> property, Annotation annotation) {
        final ValueModel model = new PropertyAdapter(property, "value", true);
        final JPanel panel = new JPanel(new BorderLayout());
        final JLabel label = new JLabel();

        model.addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                Object value = property.getValue();
                String text = null;

                if(value != null) {
                    if(value.getClass().isAnnotationPresent(org.ytoh.configurations.annotations.Component.class)) {
                        text = value.getClass().getAnnotation(org.ytoh.configurations.annotations.Component.class).name();
                    } else {
                        text = String.valueOf(value);
                    }
                }
                
                label.setText(text);
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
