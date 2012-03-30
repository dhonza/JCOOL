package org.ytoh.configurations;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.configuration.Configuration;
import org.ytoh.configurations.util.Annotations;
import org.ytoh.configurations.util.PropertyExtractor;

/**
 * A {@link ConfigurationManager} implementation configuring objects based
 * on {@link org.ytoh.configurations.annotations.Property} annotated {@link Field}s.
 *
 * @author ytoh
 */
public class PropertyConfigurationManager implements ConfigurationManager {
    private final Configuration provider;
    private final PropertyExtractor extractor;

    /**
     * Creates an instance of <code>PropertyConfigurationManager</code>.
     *
     * @param provider the {@link Configuration} to be used to retrieve
     * configuration
     * @param extractor the {@link PropertyExtractor} to be used to extract properties.
     */
    public PropertyConfigurationManager(Configuration provider, PropertyExtractor extractor) {
        this.provider = provider;
        this.extractor = extractor;
    }

    public boolean configure(Object o) {
        List<Property> properties = extractor.propertiesFor(o);
        for (Property property : properties) {
            property.configure(provider);
        }

        return true;
    }

    /**
     * Check if the supplied object can be configured by this {@link ConfigurationManager}.
     *
     * <p>The object to be configured is required to comply to the <i>JavaBeans</i>
     * convention. This <code>ConfigurationManager</code> checks whether
     * the supplied object has a public no-argument constructor and if all
     * the defined properties ({@link Field}s annotated witch
     * {@link org.ytoh.configurations.annotations.Property}) have both
     * an accessor and a mutator method.</p>
     *
     * @see ConfigurationManager#isConfigurable(java.lang.Object)
     *
     * @param o object to be configured
     * @return <code>true</code> if the object is configurable using this <code>ConfigurationManager</code>
     */
    public boolean isConfigurable(final Object o) {
        // check if o has a public no-argument constructor
        if(ConstructorUtils.getAccessibleConstructor(o.getClass(), new Class[0]) == null) {
            return false;
        }
        
        List<Field> properties = Annotations.filter(Arrays.asList(o.getClass().getDeclaredFields()), org.ytoh.configurations.annotations.Property.class);

        // check if all of the Property annotated fields have both an accessor and a mutator method
        return !CollectionUtils.exists(properties, new Predicate() {

            public boolean evaluate(Object field) {
                return !(PropertyUtils.isReadable(o, ((Field)field).getName()) && PropertyUtils.isWriteable(o, ((Field)field).getName()));
            }
        });
    }
}
