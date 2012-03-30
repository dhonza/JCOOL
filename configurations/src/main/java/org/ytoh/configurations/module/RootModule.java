package org.ytoh.configurations.module;

import java.util.*;

/**
 * Singleton root module.
 *
 * @author ytoh
 */
enum RootModule implements Module {
	/** Singleton instance */
	INSTANCE;

	/** */
	private boolean initialized = false;

	private Map<String, Module> children = new HashMap<String, Module>();

	public String getName() {
		return "/";
	}

    public Module getParent() {
		// the root module has no parent
		return null;
	}

    public Collection<? extends Module> getChildren() {
		return Collections.unmodifiableCollection(children.values());
	}

    public Collection<? extends Object> getComponents() {
		return Collections.emptyList();
	}

    public Object getComponent(String name) {
		return null;
	}

    public void register(Module module) {
		children.put(module.getName(), module);
	}

    public void dropAllChildren() {
        children = new HashMap<String, Module>();
    }

    public Collection<String> getComponentNames() {
		return Collections.emptySet();
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

    public Module findConfigurationByName(String name) {

        if (getName().compareTo(name) == 0) {
            return this;
        }

        Iterator<? extends Module> modulesIter = getChildren().iterator();
        Module cfg = null;
        while (modulesIter.hasNext()) {
            cfg = modulesIter.next();
            cfg = cfg.findConfigurationByName(name);
            if (cfg != null) {
                return cfg;
            }
        }

        return null;
    }
}
