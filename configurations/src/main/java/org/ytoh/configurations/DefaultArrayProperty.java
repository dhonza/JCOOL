/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytoh.configurations;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.validation.Validator;
import org.apache.commons.configuration.Configuration;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;
import org.jdesktop.layout.LayoutStyle;
import org.ytoh.configurations.context.PublishingContext;
import org.ytoh.configurations.ui.PropertyEditor;
import org.ytoh.configurations.ui.PropertyRenderer;

/**
 * A concrete implementation of the {@link org.ytoh.configurations.MutableProperty} interface wrapping aroung array fields.
 *
 * <p>{@link PropertyEditor}s and {@link PropertyRenderer}s defined on the
 * underling field apply for the individual array items not on this property.</p>
 *
 * @author ytoh
 */
public class DefaultArrayProperty extends AbstractProperty<Object> {

    private Object                               arrayReference;
    private List<ArrayProperty>                  arrayProperties;
    private PropertyEditor<Object, Annotation>   editor;
    private Annotation                           editorAnnotation;
    private PropertyRenderer<Object, Annotation> renderer;
    private Annotation                           rendererAnnotation;
    private PublishingContext                              context;

    /**
     * Creates an instance of <code>DefaultArrayProperty</code> wrapping an array
     * {@link Field}.
     *
     * @param annotation <code>Property</code> annotation defining this property
     * @param field underlying <code>Field</code>
     * @param arrayReference a reference to the array to wrap
     * @param validator {@link Validator} instance used to validate array items
     */
    public DefaultArrayProperty(org.ytoh.configurations.annotations.Property annotation, Field field, Object sandbox, Validator validator) {
        super("".equals(annotation.name()) ? field.getName() : annotation.name(), annotation.description(), field, sandbox, validator);
    }

    /**
     * The property is set only if a new value is provided.
     *
     * <p>Upon every new value the internal representation of this property
     * is changed. (e.g. editorComponents and rendererComponents are recreated)</p>
     *
     * @see MutableProperty#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(Object value, boolean propagate) {
        if (this.arrayProperties == null || arrayReference != value) {
            this.arrayReference = value;
            this.arrayProperties = new ArrayList<ArrayProperty>();

            for (int i = 0, size = Array.getLength(arrayReference); i < size; i++) {
                Object item = Array.get(arrayReference, i);
                ArrayProperty p = new ArrayProperty(field, sandbox, arrayReference, validator, i);
                arrayProperties.add(p);
                p.setValue(item);
            }

            editorComponent = createEditorComponent();
            rendererComponent = createRendererComponent();

            super.setValue(value, propagate);
        }
    }

    /**
     * Creates a new editor component encapsulating all inner Properties.
     *
     * @return {@link Component} that is able to modify the inner properties
     */
    private Component createEditorComponent() {
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        ParallelGroup horizontal = layout.createParallelGroup();
        SequentialGroup vertical = layout.createSequentialGroup();
        for (int i = 0, size = arrayProperties.size(); i < size; i++) {
            JLabel label = new JLabel(String.valueOf(i));
            Component e = editor.getEditorComponent(arrayProperties.get(i), editorAnnotation, context);
            horizontal.add(layout.createSequentialGroup().add(label).addPreferredGap(LayoutStyle.RELATED).add(e).addContainerGap());
            vertical.add(layout.createParallelGroup(GroupLayout.BASELINE).add(label).add(e));

            arrayProperties.get(i).setValue(Array.get(arrayReference, i), false);
        }
        layout.setHorizontalGroup(horizontal);
        layout.setVerticalGroup(vertical);

        panel.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("foreground".equals(evt.getPropertyName())) {
                    panel.setForeground((Color) evt.getNewValue());
                    for (int i = 0, count = panel.getComponentCount(); i < count; i++) {
                        panel.getComponent(i).setForeground((Color) evt.getNewValue());
                    }
                } else if ("background".equals(evt.getPropertyName())) {
                    panel.setBackground((Color) evt.getNewValue());
                    for (int i = 0, count = panel.getComponentCount(); i < count; i++) {
                        panel.getComponent(i).setBackground((Color) evt.getNewValue());
                    }
                }
            }
        });
        return panel;
    }

    /**
     * Creates a new renderer component encapsulating all inner Properties.
     *
     * @return {@link Component} that is able to display the inner properties
     */
    private Component createRendererComponent() {
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        ParallelGroup horizontal = layout.createParallelGroup();
        SequentialGroup vertical = layout.createSequentialGroup();
        for (int i = 0, size = arrayProperties.size(); i < size; i++) {
            JLabel label = new JLabel(String.valueOf(i));
            Component r = renderer.getRendererComponent(arrayProperties.get(i), rendererAnnotation);
            horizontal.add(layout.createSequentialGroup().add(label).addPreferredGap(LayoutStyle.RELATED).add(r).addContainerGap());
            vertical.add(layout.createParallelGroup(GroupLayout.BASELINE).add(label).add(r));

            arrayProperties.get(i).setValue(Array.get(arrayReference, i), false);
        }
        layout.setHorizontalGroup(horizontal);
        layout.setVerticalGroup(vertical);

        panel.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("foreground".equals(evt.getPropertyName())) {
                    panel.setForeground((Color) evt.getNewValue());
                    for (int i = 0, count = panel.getComponentCount(); i < count; i++) {
                        panel.getComponent(i).setForeground((Color) evt.getNewValue());
                    }
                } else if ("background".equals(evt.getPropertyName())) {
                    panel.setBackground((Color) evt.getNewValue());
                    for (int i = 0, count = panel.getComponentCount(); i < count; i++) {
                        panel.getComponent(i).setBackground((Color) evt.getNewValue());
                    }
                }
            }
        });
        return panel;
    }

    /**
     * Only stores the provided value for lazy instantiation.
     *
     * @see MutableProperty#setEditor(org.ytoh.configurations.ui.PropertyEditor, java.lang.annotation.Annotation, org.ytoh.configurations.context.Context)
     */
    @Override
    public <A extends Annotation> void setEditor(PropertyEditor<Object, A> editor, A annotation, PublishingContext context) {
        this.context = context;
        this.editor = (PropertyEditor<Object, Annotation>) editor;
        this.editorAnnotation = annotation;
    }

    /**
     * Only stores the provided value for lazy instantiation.
     *
     * @see MutableProperty#setRenderer(org.ytoh.configurations.ui.PropertyRenderer, java.lang.annotation.Annotation)
     */
    @Override
    public <A extends Annotation> void setRenderer(PropertyRenderer<Object, A> renderer, A annotation) {
        this.renderer = (PropertyRenderer<Object, Annotation>) renderer;
        this.rendererAnnotation = annotation;
    }

    public void store() {
        // ignore for now
    }

    public void configure(Configuration provider) {
        for (ArrayProperty arrayProperty : arrayProperties) {
            arrayProperty.configure(provider);
        }
    }

    /**
     * The inner property wrapping a single array item.
     *
     * @author ytoh
     */
    private static final class ArrayProperty extends AbstractProperty<Object> {

        /** item index */
        private final int index;
        private final Object arrayReference;

        private ArrayProperty(Field field, Object sandbox, Object arrayReference, Validator validator, int index) {
            super("", "", field, sandbox, validator);
            this.index = index;
            this.arrayReference = arrayReference;
        }

        /**
         * Stores the value to an index of the underlying array.
         *
         * @see MutableProperty#store()
         */
        public void store() {
            Object oldValue = Array.get(arrayReference, index);
            storeSupport.firePropertyChange(getFieldName(), oldValue, value);
            Array.set(arrayReference, index, value);

        }

        public void configure(Configuration provider) {
            if (Boolean.class.equals(getFieldType()) || Boolean.TYPE.equals(getFieldType())) {
                setValue(provider.getBoolean(getFieldName()));
            } else if (Byte.class.equals(getFieldType()) || Byte.TYPE.equals(getFieldType())) {
                setValue(provider.getByte(getFieldName()));
            } else if (Double.class.equals(getFieldType()) || Double.TYPE.equals(getFieldType())) {
                setValue(provider.getDouble(getFieldName()));
            } else if (Float.class.equals(getFieldType()) || Float.TYPE.equals(getFieldType())) {
                setValue(provider.getFloat(getFieldName()));
            } else if (Integer.class.equals(getFieldType()) || Integer.TYPE.equals(getFieldType())) {
                setValue(provider.getInt(getFieldName()));
            } else if (Long.class.equals(getFieldType()) || Long.TYPE.equals(getFieldType())) {
                setValue(provider.getLong(getFieldName()));
            } else if (Short.class.equals(getFieldType()) || Short.TYPE.equals(getFieldType())) {
                setValue(provider.getShort(getFieldName()));
            } else if (String.class.equals(getFieldType())) {
                setValue(provider.getString(getFieldName()));
            }
        }
    }
}
