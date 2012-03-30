package org.ytoh.configurations.ui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang.Validate;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.PropertyState;

/**
 *
 * @author ytoh
 */
public class PropertyModel extends AbstractTableModel {

    private List<Property> properties;
    private String componentTitle;

    public PropertyModel(List<Property> properties) {
        this.properties = properties;
    }

    public int getRowCount() {
        return properties.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Validate.isTrue(rowIndex >= 0 && rowIndex < properties.size(), "Row not found: " + rowIndex);
        Validate.isTrue(columnIndex == 0 || columnIndex == 1, "Column not found: " + columnIndex);

        switch (columnIndex) {
            case 0:
                return properties.get(rowIndex).getName();
            case 1:
                return properties.get(rowIndex).getValue();
            default:
                // should not happen
                throw new IllegalStateException("No column at position: " + columnIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        if(column == 0) {
            return componentTitle;
        }

        return "";
    }

    public void setComponentTitle(String componentTitle) {
        this.componentTitle = componentTitle;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(properties.get(rowIndex).getPropertyState() == PropertyState.DISABLED) {
            return false;
        }
        
        return columnIndex == 1 && properties.get(rowIndex).getEditorComponent() != null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Validate.isTrue(rowIndex >= 0 && rowIndex < properties.size(), "Row not found: " + rowIndex);
        Validate.isTrue(columnIndex == 1, "Column not mutable: " + columnIndex);

        properties.get(rowIndex).setValue(aValue);
    }
}
