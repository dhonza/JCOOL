/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.experiment.util;

import cz.cvut.felk.cig.jcool.core.*;

/**
 *
 * @author ytoh
 */
public interface Wrapper<T,E> extends Consumer<T>, Producer<E> { }
