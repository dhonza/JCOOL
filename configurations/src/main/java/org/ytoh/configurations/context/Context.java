/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytoh.configurations.context;

import java.util.List;

/**
 * A Context is a container of <code>String</code> keyed {@link List}s
 * that provide dynamic options.
 *
 * @author ytoh
 */
public interface Context {

    /**
     * Retrieve a {@link List} of options based on the option type and key.
     *
     * @param type type of the options to be retrieved
     * @param key string key of the typed option list
     * @return a list of typed options
     */
    <T> List<T> getList(Class<T> type, String key);

    /**
     * 
     * @param <T>
     * @param type
     * @param key
     * @return
     */
    <T> T get(Class<T> type, String key);
}
