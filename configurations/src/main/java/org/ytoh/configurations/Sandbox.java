package org.ytoh.configurations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Sandbox listens for property changes and reloads other properties if any
 * side-effects changed their underlying {@link Field}s.
 *
 * @author ytoh
 */
public class Sandbox implements PropertyChangeListener {

    /** property references */
    private List<AbstractProperty> properties;
    /** object declaring the properties */
    private Object sandbox;

    /**
     * Factory method creating a sandboxed version of the supplied object.
     *
     * @param sandbox object to sandbox
     * @return a sandboxed object
     */
    public static final Sandbox newInstance(Object sandbox) {
        return new Sandbox(sandbox);
    }

    /**
     * Create a <code>Sandbox</code> instance based on the supplied object.
     *
     * @param sandbox
     */
    private Sandbox(Object sandbox) {
        this();
        this.sandbox = sandbox;
    }

    /**
     * Create an empty <code>Sandbox</code> instance.
     */
    private Sandbox() {
        this.properties = new ArrayList<AbstractProperty>();
    }

    /**
     * Adds properties to this sandbox.
     * 
     * @param property
     */
    public void addProperty(AbstractProperty property) {
        property.addStorePropertyChangeListener(this);
        properties.add(property);
    }

    /**
     * Listen for property changes and detect side-effects. If any side-effects
     * are detected reload the relevant properties.
     * 
     * @see PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent) 
     */
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            for (AbstractProperty defaultProperty : properties) {
                if (evt.getSource() != defaultProperty) {
                    defaultProperty.setValue(PropertyUtils.getProperty(sandbox, defaultProperty.getFieldName()), false);
                    // if the property has a state associated try updating the property state
                    String fieldName = defaultProperty.getFieldName();
                    // workaround: PropertyUtils does not property detect the state property for single letter properties
                    if (fieldName.length() == 1) {
                        fieldName = fieldName.toUpperCase();
                    }
                    if (PropertyUtils.isReadable(sandbox, fieldName + "State")) {
                        defaultProperty.setPropertyState((PropertyState) PropertyUtils.getProperty(sandbox, fieldName + "State"));
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Sandbox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Sandbox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Sandbox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
