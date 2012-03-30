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
public interface Subscriber<T> {

    <E extends Publisher> void notifyOf(E publisher, List<? extends T> value, String key);
}
