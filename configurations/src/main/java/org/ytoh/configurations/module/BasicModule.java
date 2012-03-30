package org.ytoh.configurations.module;

import java.util.*;

import org.apache.commons.lang.Validate;

/**
 *
 * @author ytoh
 */
public class BasicModule implements Module {
    private String name;
    transient private Module parent;
    private Map<String, Module> children;
    private Map<String, Object> components;

    private BasicModule(BasicModuleBuilder builder) {
        this.name           = builder.name;
        this.parent         = builder.parent;
        this.children       = builder.children;
        this.components     = builder.components;
    }

    public static BasicModuleBuilder withName(String name, Module parent) {
        return new BasicModuleBuilder(name, parent);
    }

    public String getName() {
        return name;
    }

    public Module getParent() {
        return parent;
    }

    public Collection<? extends Module> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    public Collection<? extends Object> getComponents() {
        return Collections.unmodifiableCollection(components.values());
    }

    public Object getComponent(String name) {
        return components.get(name);
    }

    public void register(Module module) {
        children.put(module.getName(), module);
    }

    public Module findConfigurationByName(String name) {

        if (this.name.compareTo(name) == 0) {
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

    public Collection<String> getComponentNames() {
		return Collections.unmodifiableSet(components.keySet());
	}

	/**
	 *
	 */
    public static final class BasicModuleBuilder {
        private String name;                        // required
        private Module parent;                      // required
        private Map<String, Module> children;              // optional
        private Map<String, Object> components;            // optional

        private BasicModuleBuilder(String name, Module parent) {
            this.name = name;
            this.parent = parent;
            this.components = new HashMap<String, Object>();
            this.children = new HashMap<String, Module>();
        }

        public BasicModuleBuilder withComponent(Object component, String name) {
            Validate.notNull(component, "component cannot be null");

            components.put(name, component);
            return this;
        }

        public BasicModuleBuilder withComponent(Object component) {
            return withComponent(component, component.getClass().getSimpleName().toLowerCase());
        }

        public BasicModule build() {
            BasicModule module = new BasicModule(this);
            this.parent.register(module);

            return module;
        }
    }
}
