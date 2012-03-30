package org.ytoh.configurations.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.util.AnnotationPropertyExtractor;
import org.ytoh.configurations.util.PropertyExtractor;

/**
 * A {@link JTable} that extracts properties from the supplied object
 * and displays them in a two column table.
 *
 * @author ytoh
 */
public class PropertyTable extends JTable {
    /** extracted properties */
    private List<Property>              properties;
    /** property editors */
    private List<PropertyCellEditor>    editors;
    /** property renderers */
    private List<PropertyCellRenderer>  renderers;

    private List<DescriptionCellRenderer> descriptions;

    /**
     * Creates a <code>PropertyTable</code> that extracts the properties from
     * Object <code>o</code> using the supplied {@link PropertyExtractor}.
     *
     * @see PropertyExtractor
     * 
     * @param o object to display
     * @param extractor to use to extract properties from <code>o</code>
     */
    public PropertyTable(Object o, PropertyExtractor extractor) {
        this.properties = extractor.propertiesFor(o);
        PropertyModel model = new PropertyModel(properties);

        if(o.getClass().isAnnotationPresent(Component.class)) {
            Component annotation = o.getClass().getAnnotation(Component.class);
            model.setComponentTitle(annotation.name());
            if(annotation.description().trim().length() > 0) {
                getTableHeader().setToolTipText(annotation.description());
            }
        } else {
            model.setComponentTitle(o.getClass().getSimpleName());
        }

        this.setModel(model);

        this.editors = new ArrayList<PropertyCellEditor>(CollectionUtils.collect(properties, new Transformer() {

            public Object transform(Object input) {
                return new PropertyCellEditor((Property) input);
            }
        }));

        this.renderers = new ArrayList<PropertyCellRenderer>(CollectionUtils.collect(properties, new Transformer() {

            public Object transform(Object input) {
                return new PropertyCellRenderer((Property) input);
            }
        }));

        this.descriptions = new ArrayList<DescriptionCellRenderer>(CollectionUtils.collect(properties, new Transformer() {

            public Object transform(Object input) {
                return new DescriptionCellRenderer((Property)input);
            }
        }));
    }

    /**
     * Creates a <code>PropertyTable</code> that extracts the properties from
     * Object <code>o</code> using a default PropertyExtractor.
     *
     * <p>This method uses {@link AnnotationPropertyExtractor} as the default.</p>
     *
     * @param o object to display
     */
    public PropertyTable(Object o) {
        this(o, new AnnotationPropertyExtractor());
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if(column == 1) {
            return editors.get(row);
        }

        return super.getCellEditor(row, column);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if(column == 1) {
            return renderers.get(row);
        }
        DescriptionCellRenderer description = descriptions.get(row);

        description.setDelegate(super.getCellRenderer(row, column));

        return description;
    }
}
