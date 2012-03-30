/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.core;

/**
 * A thread-safe implementation of the <code>Telemetry</code> interface capable
 * of externalizing a single <code>double</code> value.
 *
 * @author ytoh
 */
public final class ValueTelemetry implements Telemetry<Double> {

    // internal representation
    private final double value;

    public ValueTelemetry() {
        value = Double.NaN;
    }

    /**
     * Publish a double as the internal state/telemetry.
     *
     * @param value double to be stored
     */
    public ValueTelemetry(Double value) {
        this.value = value;
    }

    /**
     * @return value the stored double value
     */
    public Double getValue() {
        return value;
    }
}
