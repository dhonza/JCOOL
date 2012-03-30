/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.validation.Validator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration.Configuration;
import org.ytoh.configurations.context.PublishingContext;
import org.ytoh.configurations.ui.PropertyEditor;
import org.ytoh.configurations.ui.PropertyRenderer;

/**
 * A concrete implementation of the {@link org.ytoh.configurations.MutableProperty} interface wrapping aroung component fields.
 *
 * <p>{@link PropertyEditor}s and {@link PropertyRenderer}s defined on the
 * underling field apply for the underlying component not on this property.</p>
 *
 * @author ytoh
 */
public class DefaultComponentProperty extends AbstractProperty<Object> {

    private PropertyEditor<Object, Annotation>   editor;
    private Annotation                           editorAnnotation;
    private PropertyRenderer<Object, Annotation> renderer;
    private Annotation                           rendererAnnotation;
    private PublishingContext                    context;

    /**
     * Creates an instance of <code>DefaultComponentProperty</code> wrapping a component
     * {@link Field}.
     *
     * @param annotation <code>Property</code> annotation defining this property
     * @param field underlying <code>Field</code>
     * @param sandbox a reference to the defining object
     * @param validator {@link Validator} instance used to validate the component with
     */
    public DefaultComponentProperty(org.ytoh.configurations.annotations.Property annotation, Field field, Object sandbox, Validator validator) {
        super("".equals(annotation.name()) ? field.getName() : annotation.name(), annotation.description(), field, sandbox, validator);
    }

    /**
     * The property is set only if a new value is provided.
     *
     * <p>Upon every new value the internal representation of this property
     * is changed. (e.g. editorComponent and rendererComponent are recreated)</p>
     *
     * @see MutableProperty#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(Object value, boolean propagate) {
        if(this.value != value) {
            // remember old and set the new value
            Object oldValue = this.value;
            this.value = value;
            // propagate the change to the sandox object to force sideeffect
            if (propagate) {
                store();
            }
            editorComponent = editor.getEditorComponent(this, editorAnnotation, context);
            rendererComponent = renderer.getRendererComponent(this, rendererAnnotation);
            // notify listeners a change has occured
            support.firePropertyChange("value", oldValue, this.value);
        }
    }

    /**
     * Lazily instantiates and retrieves a {@link Component} to edit
     * the underlying component as a {@link Property}.
     *
     * @see MutableProperty#getEditorComponent()
     * @return {@link Component} to be used to edit this property
     */
    @Override
    public <A extends Annotation> void setEditor(PropertyEditor<Object, A> editor, A annotation, PublishingContext context) {
        this.editor = (PropertyEditor<Object, Annotation>) editor;
        this.editorAnnotation = annotation;
        this.context = context;
    }

    /**
     * Lazily instantiates and retrieves a {@link Component} to render
     * the underlying component as a {@link Property}.
     *
     * @see MutableProperty#getRendererComponent()
     * @return {@link Component} to be used to render this property
     */
    @Override
    public <A extends Annotation> void setRenderer(PropertyRenderer<Object, A> renderer, A annotation) {
        this.renderer = (PropertyRenderer<Object, Annotation>) renderer;
        this.rendererAnnotation = annotation;
        rendererComponent = this.renderer.getRendererComponent(this, rendererAnnotation);
    }

    public void store() {
        try {
            Object oldValue = PropertyUtils.getProperty(sandbox, getFieldName());
            PropertyUtils.setProperty(sandbox, getFieldName(), value);
            storeSupport.firePropertyChange(getFieldName(), oldValue, value);
        } catch (IllegalAccessException ex) {
            throw new ConfigurationException("Could not eagerly set value", ex);
        } catch (InvocationTargetException ex) {
            throw new ConfigurationException("Could not eagerly set value", ex);
        } catch (NoSuchMethodException ex) {
            throw new ConfigurationException("Could not eagerly set value", ex);
        }
    }

    public void configure(Configuration provider) {
        System.out.println("co teraz?");
    }
}
