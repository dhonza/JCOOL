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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.validation.Validator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration.Configuration;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;
import org.jdesktop.layout.LayoutStyle;
import org.ytoh.configurations.context.PublishingContext;
import org.ytoh.configurations.ui.PropertyEditor;
import org.ytoh.configurations.ui.PropertyRenderer;

/**
 * A concrete implementation of the {@link org.ytoh.configurations.MutableProperty} interface wrapping aroung {@link List} fields.
 *
 * <p>{@link PropertyEditor}s and {@link PropertyRenderer}s defined on the
 * underling field apply for the individual list items not on this property.</p>
 *
 * @author ytoh
 */
public class DefaultListProperty extends AbstractProperty<Object> {

    private List listValue;
    private List<ListProperty> listProperties;
    private PropertyEditor<Object, Annotation> editor;
    private Annotation editorAnnotation;
    private PropertyRenderer<Object, Annotation> renderer;
    private Annotation rendererAnnotation;
    private PublishingContext context;

    /**
     * Creates an instance of <code>DefaultListProperty</code> wrapping a {@link List}
     * {@link Field}.
     *
     * @param annotation <code>Property</code> annotation defining this property
     * @param field underlying <code>Field</code>
     * @param sandbox a reference to the defining object
     * @param validator {@link Validator} instance used to validate list items
     */
    public DefaultListProperty(org.ytoh.configurations.annotations.Property annotation, Field field, Object sandbox, Validator validator) {
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
        if(value != null) {
            List list = (List) value;

            if(this.listProperties == null || listValue != value) {
                listValue = list;
                create(list);
            }

            for (int i = 0; i < list.size(); i++) {
                listProperties.get(i).setValue(list.get(i));
            }

            store();
        }

        support.firePropertyChange(getFieldName(), Collections.emptyList(), listValue);
    }

    private void create(List list) {
        this.listProperties = new ArrayList<ListProperty>();

        for (int i = 0, size = list.size(); i < size; i++) {
            Object item = list.get(i);
            ListProperty p = new ListProperty(field, sandbox, list, validator, i);
            listProperties.add(p);
            p.setValue(item);
        }

        editorComponent = createEditorComponent();
        rendererComponent = createRendererComponent();

//        super.setValue(value, propagate);
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
        for (int i = 0, size = listProperties.size(); i < size; i++) {
            JLabel label = new JLabel(String.valueOf(i));
            Component e = editor.getEditorComponent(listProperties.get(i), editorAnnotation, context);
            horizontal.add(layout.createSequentialGroup().add(label).addPreferredGap(LayoutStyle.RELATED).add(e).addContainerGap());
            vertical.add(layout.createParallelGroup(GroupLayout.BASELINE).add(label).add(e));
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
        for (int i = 0, size = listProperties.size(); i < size; i++) {
            JLabel label = new JLabel(String.valueOf(i));
            Component r = renderer.getRendererComponent(listProperties.get(i), rendererAnnotation);
            horizontal.add(layout.createSequentialGroup().add(label).addPreferredGap(LayoutStyle.RELATED).add(r).addContainerGap());
            vertical.add(layout.createParallelGroup(GroupLayout.BASELINE).add(label).add(r));
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
        this.editor = (PropertyEditor<Object, Annotation>) editor;
        this.editorAnnotation = annotation;
        this.context = context;
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
        try {
            PropertyUtils.setProperty(sandbox, getFieldName(), listValue);
            storeSupport.firePropertyChange(getFieldName(), Collections.emptyList(), listValue);
        } catch (IllegalAccessException ex) {
            throw new ConfigurationException("Could not eagerly set value", ex);
        } catch (InvocationTargetException ex) {
            throw new ConfigurationException("Could not eagerly set value", ex);
        } catch (NoSuchMethodException ex) {
            throw new ConfigurationException("Could not eagerly set value", ex);
        }
    }

    public void configure(Configuration provider) {
        for (ListProperty listProperty : listProperties) {
            listProperty.configure(provider);
        }
    }

    /**
     * The inner property wrapping a single list item.
     */
    private static class ListProperty extends AbstractProperty<Object> {

        /** item index */
        private int index;
        /** list reference */
        private List list;

        private ListProperty(Field field, Object sandbox, List list, Validator validator, int index) {
            super("", "", field, sandbox, validator);
            this.index = index;
            this.list = list;
        }

        /**
         * Stores the value to an index of the underlying list.
         *
         * @see MutableProperty#store()
         */
        public void store() {
            Object oldValue = list.get(index);
            list.set(index, value);
            storeSupport.firePropertyChange(getFieldName(), oldValue, value);
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
