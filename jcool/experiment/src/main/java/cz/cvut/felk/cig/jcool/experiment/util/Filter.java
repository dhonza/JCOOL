/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.experiment.util;

import cz.cvut.felk.cig.jcool.core.*;
import java.util.List;

/**
 *
 * @author ytoh
 */
public interface Filter<T, E extends T> extends Consumer<List<? extends T>>, Producer<E> { }
