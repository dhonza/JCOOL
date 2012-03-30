package org.ytoh.configurations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.validation.Validator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration.Configuration;

/**
 * The default implementation of the {@link Property} interface.
 *
 * <p>An annotation based container for the changes made during the interactive
 * configuration of the underlying component. It uses the provided
 * {@link Validator} instance to validate the value currently being assigned
 * to the underlying field and produce validation messages.</p>
 *
 * @author ytoh
 */
public class DefaultProperty extends AbstractProperty<Object> {

    /**
     * Constructs and initializes an instance of <code>DefaultProperty</code>.
     *
     * @param annotation {@link org.ytoh.configurations.annotations.Property} instance
     * marking the underlying field as a property
     * @param field the underlying field
     * @param type the class of the object owning this property
     * @param validator the validator instance used to validate the property value
     */
    public DefaultProperty(org.ytoh.configurations.annotations.Property annotation, Field field, Object sandbox, Validator validator) {
        super("".equals(annotation.name()) ? field.getName() : annotation.name(), annotation.description(), field, sandbox, validator);
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
}
