package org.ytoh.configurations.module;

import org.apache.commons.configuration.Configuration;

/**
 *
 * @author ytoh
 */
public interface ModuleDeserializer {

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
	 * @param parent
	 * @return
	 */
	public void deserializeModule(Module parent);
}
