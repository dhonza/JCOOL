/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytoh.configurations;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.ytoh.configurations.context.PublishingContext;
import org.ytoh.configurations.ui.PropertyEditor;
import org.ytoh.configurations.ui.PropertyRenderer;

/**
 * A skeletal implementation of the {@link MutableProperty} interface.
 *
 * <p>It takes care of storing the necessary information for visualization
 * and value change detection.</p>
 *
 * @author ytoh
 */
public abstract class AbstractProperty<T> implements MutableProperty<T> {

    /** {@link Component} being used to edit this property */
    protected Component editorComponent;
    /** {@link Component} being used to render this property */
    protected Component rendererComponent;
    /** A human readable property name */
    protected String name;
    /** A human readable property description */
    protected String description;
    /** The current value of this property */
    protected T value;
    protected Field field;
    /** The current messages reporting value constraint violations */
    protected List<String> violationMessages;
    /** The validator instance used to validate property value */
    protected Validator validator;
    /** The playground in which this property is defined */
    protected Object sandbox;
    /** The current property state */
    protected PropertyState state;
    /** Property change support for easy {@link Component} binding */
    protected PropertyChangeSupport support = new PropertyChangeSupport(this);
    /** Property change support for easy sideffect detection */
    protected PropertyChangeSupport storeSupport = new PropertyChangeSupport(this);

    /**
     * Creates an instance of <code>AbstractProperty</code>.
     * 
     * @param name human readable name of the property
     * @param description human readable description of the property
     * @param field underlying field for the property
     * @param sandbox object used for side-effect detection
     * @param validator {@link Validator} for value validation
     */
    public AbstractProperty(String name, String description, Field field, Object sandbox, Validator validator) {
        this.name = StringUtils.defaultString(name);
        this.description = StringUtils.defaultString(description);
        this.field = field;
        this.sandbox = sandbox;
        this.state = PropertyState.ENABLED;
        this.validator = validator;
        this.violationMessages = new ArrayList<String>();
    }

    public Object getContext() {
        return sandbox;
    }

    public String getDescription() {
        return description;
    }

    public Component getEditorComponent() {
        return editorComponent;
    }

    public String getFieldName() {
        return field.getName();
    }

    public Class<?> getFieldType() {
        return field.getType();
    }

    public String getName() {
        return name;
    }

    public PropertyState getPropertyState() {
        return state;
    }

    public Component getRendererComponent() {
        return rendererComponent;
    }

    public T getValue() {
        return value;
    }

    public String[] getViolationMessages() {
        return violationMessages.toArray(new String[violationMessages.size()]);
    }

    public void setValue(T value) {
        this.setValue(value, true);
    }

    public void setValue(T value, boolean propagate) {
        // validations
        violationMessages = (List<String>) CollectionUtils.collect(validator.validateValue(field.getDeclaringClass(), field.getName(), value, new Class[0]), new Transformer() {

            public Object transform(Object input) {
                return ((ConstraintViolation) input).getMessage();
            }
        });
        // remember old and set the new value
        Object oldValue = this.value;
        this.value = value;
        // propagate the change to the sandox object to force sideeffect
        if (propagate) {
            store();
        }
        // notify listeners a change has occured
        support.firePropertyChange("value", oldValue, this.value);
    }

    public <A extends Annotation> void setEditor(PropertyEditor<T, A> editor, A annotation, PublishingContext context) {
        editorComponent = editor.getEditorComponent(this, annotation, context);
    }

    public <A extends Annotation> void setRenderer(PropertyRenderer<T, A> renderer, A annotation) {
        rendererComponent = renderer.getRendererComponent(this, annotation);
    }

    public void setPropertyState(PropertyState state) {
        PropertyState oldState = this.state;
        this.state = state;
        getEditorComponent().setEnabled(state == PropertyState.ENABLED);
        support.firePropertyChange("propertyState", oldState, this.state);
    }

    //////////////////////////////
    // Property change support
    //////////////////////////////
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public void addStorePropertyChangeListener(PropertyChangeListener listener) {
        this.storeSupport.addPropertyChangeListener(listener);
    }

    public void removeStorePropertyChangeListener(PropertyChangeListener listener) {
        this.storeSupport.removePropertyChangeListener(listener);
    }
}
