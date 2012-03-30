package org.ytoh.configurations.module;

import org.apache.commons.configuration.Configuration;

import java.beans.PersistenceDelegate;

/**
 *
 * @author ytoh
 */
public interface ModuleSerializer {

	/**
	 *
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration);

    /**
     * Sets confiruation base directory.
     * @param path Path to directory with configuration files
     */
    public void setConfigurationDirectory(String path);

    /**
     * Returns confiruation base directory.
     * @return A path to directory with configuration.
     */
    public String getConfigurationDirectory();


	/**
	 *
	 * @param module
	 */
	public void serializeModule(Module module);

    /**
     * Adds special handler for XML serializer for given class 
     * @param servedType Class to be served by this delegate.
     * @param delegate Delegate class instance
     */
    public void addNewDelegate(Class<?> servedType, PersistenceDelegate delegate);
}
