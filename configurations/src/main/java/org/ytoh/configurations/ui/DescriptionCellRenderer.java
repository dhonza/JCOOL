/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytoh.configurations.ui;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.ytoh.configurations.Property;

/**
 *
 * @author ytoh
 */
public class DescriptionCellRenderer implements TableCellRenderer {

    private final Property property;
    private TableCellRenderer delegate;

    public DescriptionCellRenderer(Property property) {
        this.property = property;
    }

    public void setDelegate(TableCellRenderer delegate) {
        this.delegate = delegate;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (component instanceof JComponent && property.getDescription().trim().length() > 0) {
            ((JComponent) component).setToolTipText(property.getDescription());
        }
        return component;
    }
}
