package org.ytoh.configurations;

import java.awt.Component;
import org.apache.commons.configuration.Configuration;

/**
 * Represents a configurable field on an object.
 *
 * <p>This representation allows the field to be configured either automatically
 * or interactively. The interactive configuration uses a renderer and an editor
 * {@link Component} to display and modify the current value of the underlying
 * field. The automatic configuration uses a {@link ConfigurationProvider}
 * to retrieve the necessary values. 
 * <strong>NOTE:</strong> All values are validated and the changes are buffered
 * <i>except</i> for eager properties.</p>
 * 
 * @see DefaultProperty
 * @see Property#isEager()
 *
 * @author ytoh
 */
public interface Property<T> {

    /**
     * Used for interactive configuration to display an editor (a component
     * making it possible to change the property value).
     *
     * @return a {@link Component} that is used to set the desired property value
     */
    Component getEditorComponent();

    /**
     * Used for interactive configuration to display a renderer (a component
     * able to visualize the current property value).
     *
     * @return a {@link Component} that is used to visualize the property value
     */
    Component getRendererComponent();

    /**
     * Used for interactive configuration to retrieve a human readable property
     * name.
     *
     * @return a <code>String</code> containing a human readable name
     */
    String getName();

    /**
     * Used for interactive configuration to retrieve a human readable property
     * description.
     *
     * @return a <code>String</code> containing a human readable description
     */
    String getDescription();

    /**
     * Retrieves an array of messages reporting any detected constraint
     * violations.
     *
     * @return an array of constraint violation messages
     */
    String[] getViolationMessages();

    /**
     * Used for automatic configuration to retrieve the property value using
     * a {@link Configuration}.
     *
     * @param provider the <code>Configuration</code> that will be used
     * to retrieve the value
     */
    @Deprecated
    void configure(Configuration provider);

    /**
     * Returns the current state of this property.
     *
     * @return current property state
     */
    PropertyState getPropertyState();

    /**
     * Returns the current property value.
     *
     * @return current property value
     */
    T getValue();

    /**
     * Sets the property value.
     *
     * @param value to set the property to
     */
    void setValue(T value);
}
