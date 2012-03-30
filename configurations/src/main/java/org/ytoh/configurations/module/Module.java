package org.ytoh.configurations.module;

import java.util.Collection;

/**
 *
 * @author ytoh
 */
public interface Module {

	/**
	 *
	 * @return
	 */
    String getName();

	/**
	 *
	 * @return
	 */
    Module getParent();

	/**
	 *
	 * @return
	 */
    Collection<? extends Module> getChildren();

	/**
	 *
	 * @return
	 */
    Collection<? extends Object> getComponents();

	/**
	 *
	 * @return
	 */
	Collection<String> getComponentNames();

	/**
	 *
	 * @param name
	 * @return
	 */
    Object getComponent(String name);

	/**
	 *
	 * @param module
	 */
    void register(Module module);

    Module findConfigurationByName(String name);
}
