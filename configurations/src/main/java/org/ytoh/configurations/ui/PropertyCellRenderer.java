package org.ytoh.configurations.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.PropertyState;

/**
 *
 * @author ytoh
 */
public class PropertyCellRenderer implements TableCellRenderer {

    private Property property;
    private JPanel panel;

    public PropertyCellRenderer(Property property) {
        this.property = property;
        panel = new JPanel(new GridLayout(0,1));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component rendererComponent = property.getRendererComponent();
        if (isSelected) {
            rendererComponent.setForeground(table.getSelectionForeground());
            rendererComponent.setBackground(table.getSelectionBackground());
            panel.setBackground(table.getSelectionBackground());
        } else {
            rendererComponent.setForeground(table.getForeground());
            rendererComponent.setBackground(table.getBackground());
            panel.setBackground(table.getBackground());
        }
        
        if(property.getPropertyState() == PropertyState.DISABLED) {
            rendererComponent.setForeground(Color.gray);
        }

        panel.removeAll();
        panel.add(rendererComponent);

        for (String message : property.getViolationMessages()) {
            JLabel label = new JLabel(message);
            label.setForeground(Color.red);
            panel.add(label);
        }

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (table.getRowHeight(row) != panel.getPreferredSize().height) {
            table.setRowHeight(row, panel.getPreferredSize().height);
        }

        return panel;
    }
}
