/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytoh.configurations.context;

import java.util.List;

/**
 *
 * @author ytoh
 */
public interface MutableContext extends Context {

    /**
     * Registers a list of options of a certain type with a certain
     * <code>String</code> key in the internal registry.
     *
     * @param type of the options to register
     * @param options list of options to register
     * @param key <code>String</code> key to register options under
     */
    <T> void register(Class<T> type, List<? extends T> options, String key);

    /**
     *
     * @param type
     * @param value
     * @param key
     */
    <T, E extends T> void register(Class<T> type, E value, String key);
}
