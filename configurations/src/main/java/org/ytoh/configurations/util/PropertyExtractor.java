package org.ytoh.configurations.util;

import java.lang.reflect.Field;
import java.util.List;
import org.ytoh.configurations.Property;

/**
 * <p>Property extractor is an object that can retrieve properties for any given
 * object. These properties it represents as instances of {@link Property}.</p>
 *
 * @author ytoh
 */
public interface PropertyExtractor {

    /**
     * <p>Performs a check whether the given field can be viewed as a property
     * or not.</p>
     *
     * @param field to be analyzed
     * @return <code>true</code> if the given field can be viewed as a property
     * <code>false</code> otherwise
     */
    boolean isProperty(Field field);

    /**
     * <p>Retrieves a list of properties for a given object.</p>
     *
     * @param o to retrieve properties from
     * @return a list of {@link Property} instances
     */
    List<Property> propertiesFor(Object o);
}
