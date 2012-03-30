/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations;

import java.lang.annotation.Annotation;
import org.ytoh.configurations.context.PublishingContext;
import org.ytoh.configurations.ui.PropertyEditor;
import org.ytoh.configurations.ui.PropertyRenderer;

/**
 * An extension to the {@link Property} interface adding special mutator methods
 * and a set of specialized accessor methods related to the underlying objects.
 *
 * @author ytoh
 */
public interface MutableProperty<T> extends Property<T> {

    /**
     * Returns this properties context.
     *
     * <p>The property contexts is the object on which the property is
     * declared in.</p>
     *
     * @return object containing this property
     */
    Object getContext();

    /**
     * Returns the name underlying field.
     *
     * @return name of the underlying field
     */
    String getFieldName();

    /**
     * Returns the class of the underlying field.
     *
     * @return class of the underlying field
     */
    Class<?> getFieldType();

    /**
     * Sets the value of this property and persists it in the underlying field
     * if <code>propagate</code> is <code>true</code>.
     *
     * @param value to be set
     * @param propagate a flag representing if the new value should be propagated
     * to the underlying field or not
     */
    void setValue(T value, boolean propagate);

    /**
     * Sets the property state.
     * 
     * @param state the new property state
     */
    void setPropertyState(PropertyState state);

    /**
     * Sets the {@link PropertyEditor} used to retrieve the {@link Component}
     * to be used to edit this property.
     *
     * @param editor <code>PropertyEditor</code> instance used to get
     * the editor component
     * @param annotation {@link Annotation} instance defining
     * the <code>PropertyEditor</code>
     * @param context dynamic {@link Context} used to setup a dynamic editor
     */
    <A extends Annotation> void setEditor(PropertyEditor<T,A> editor, A annotation, PublishingContext context);

    /**
     * Sets the {@link PropertyRenderer} used to retrieve the {@link Component}
     * to be used to render this property.
     * 
     * @param renderer <code>PropertyRenderer</code> instance used to get
     * the renderer component
     * @param annotation {@link Annotation} instance defining
     * the <code>PropertyRenderer</code>
     */
    <A extends Annotation> void setRenderer(PropertyRenderer<T,A> renderer, A annotation);

    /**
     * Persists the current property value in the underlying field.
     */
    void store();
}
