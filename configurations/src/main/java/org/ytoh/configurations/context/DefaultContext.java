/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytoh.configurations.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;

/**
 * A typesafe implementation of the {@link Context} interface.
 *
 * <p>This implementation uses a {@link Map} of <code>Map</code>s with lazy
 * initialization to store and retrieve the option lists.</p>
 *
 * @author ytoh
 */
public class DefaultContext implements MutableContext {

    /** an internal registry of option lists */
    private Map<Class<?>, Map<String, List<?>>> listRegistry;

    /** an internal registry */
    private Map<Class<?>, Map<String, Object>> registry;

    /**
     * Creates a default instance of {@link DefaultContext}
     */
    public DefaultContext() {
        listRegistry = LazyMap.decorate(new HashMap<Class<?>, Map<String, List<?>>>(), new Factory() {

            public Object create() {
                return LazyMap.decorate(new HashMap<String, List<?>>(), new Factory() {

                    public Object create() {
                        return new ArrayList();
                    }
                });
            }
        });

        registry = LazyMap.decorate(new HashMap<Class<?>, Map<String, ?>>(), new Factory() {

            public Object create() {
                return new HashMap<String, Object>();
            }
        });
    }

    /**
     * 
     * @param type
     * @param key
     * @return
     */
    public <T> List<T> getList(Class<T> type, String key) {
        return (List<T>) listRegistry.get(type).get(key);
    }

    /**
     * Registers a list of options of a certain type with a certain
     * <code>String</code> key in the internal registry.
     *
     * @param type of the options to register
     * @param options list of options to register
     * @param key <code>String</code> key to register options under
     */
    public <T> void register(Class<T> type, List<? extends T> options, String key) {
        listRegistry.get(type).put(key, options);
    }

    /**
     *
     * @param type
     * @param value
     * @param key
     */
    public <T, E extends T> void register(Class<T> type, E value, String key) {
        registry.get(type).put(key, value);
    }

    public <T> T get(Class<T> type, String key) {
        return type.cast(registry.get(type).get(key));
    }
}
