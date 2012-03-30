/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.ui;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JPanel;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.context.PublishingContext;

/**
 *
 * @author ytoh
 */
public class PropertyListEditor implements PropertyEditor<List<?>, org.ytoh.configurations.annotations.List> {

    public Component getEditorComponent(Property<List<?>> property, org.ytoh.configurations.annotations.List annotation, PublishingContext context) {
        Validate.notNull(property.getValue(), "list property null.");
        Validate.isTrue(property.getValue() instanceof List, "property value does not hold a list.");

        final JPanel panel = new JPanel(new GridLayout(0, 1));

        CollectionUtils.forAllDo(property.getValue(), new Closure() {

            public void execute(Object i) {
                panel.add(new PropertyTable(i));
            }
        });

        return panel;
    }
}
