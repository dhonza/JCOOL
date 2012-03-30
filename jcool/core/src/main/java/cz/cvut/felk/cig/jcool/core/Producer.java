/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.core;

/**
 *
 * @author ytoh
 */
public interface Producer<T> {

    void addConsumer(Consumer<? super T> consumer);

    T getValue();
}
