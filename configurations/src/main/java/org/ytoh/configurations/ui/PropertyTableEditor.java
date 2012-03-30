/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.annotations.Table;
import org.ytoh.configurations.context.PublishingContext;

/**
 * A {@link PropertyEditor} implementation modifying the underlying {@link Property}
 * with a {@link PropertyTable}.
 *
 * @author ytoh
 */
public class PropertyTableEditor implements PropertyEditor<Object, Table> {

    public Component getEditorComponent(Property<Object> property, Table annotation, PublishingContext context) {
        JPanel panel = new JPanel(new BorderLayout());
        PropertyTable table = new PropertyTable(property.getValue());
        panel.add(table, BorderLayout.CENTER);
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        return panel;
    }
}
