/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.experiment;

import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.Telemetry;

/**
 *
 * @author ytoh
 */
public interface TelemetryVisualization<T extends Telemetry> extends Consumer<Iteration<T>>, Visualization<T> {

    /**
     * 
     * @param function
     * @param method
     */
    void init(Function function);
}
