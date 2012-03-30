package org.ytoh.configurations;

/**
 * A configuration manager is responsible for setting object marked properties
 * to their configured values.
 *
 * @author ytoh
 */
public interface ConfigurationManager {

    /**
     * If the supplied object is configurable, performs its configuration.
     *
     * @param o
     * @return <code>true</code> if the object has been succesfully configured
     */
    boolean configure(Object o);

    /**
     * Performs a check whether the object can be configured using this
     * configuration manager or not.
     *
     * @param o to check
     * @return <code>true</code> if object can be configure with this
     * configuration manager
     */
    boolean isConfigurable(Object o);
}
