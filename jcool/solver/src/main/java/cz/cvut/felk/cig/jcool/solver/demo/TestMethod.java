/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.solver.demo;

import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.SingleSolution;
import cz.cvut.felk.cig.jcool.core.Solution;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.core.ValueTelemetry;
import java.util.Random;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.OneOf;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Simple method demonstrating automatic objective function method call 
 * statistics calculation.
 *
 * @author ytoh
 */
@Component(name="Test method")
public class TestMethod implements OptimizationMethod<ValueTelemetry> {
    private Random r = new Random();
    private ObjectiveFunction function;

    //sensible default
    @Property
    @Range(from=1, to=2)
    private int valueAtThreshold = 3;

    @Property
    @Range(from=-2, to=10)
    private double testDouble = 0.0;

    @Property
    private boolean use = false;

    @Property
    @OneOf({"a","b","c"})
    private String optionString = "c";

    private boolean stop = false;
    private double value;

    public double getTestDouble() {
        return testDouble;
    }

    public void setTestDouble(double testDouble) {
        this.testDouble = testDouble;
    }

    public String getOptionString() {
        return optionString;
    }

    public void setOptionString(String optionString) {
        this.optionString = optionString;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    private int x;

    public int getX() {
        return x;
    }

    public void setValueAtThreshold(int valueAtThreshold) {
        this.valueAtThreshold = valueAtThreshold;
    }

    public int getValueAtThreshold() {
        return valueAtThreshold;
    }

    public void init(ObjectiveFunction function) {
        // initialize
        this.function = function;
    }

    
    public void optimize() throws OptimizationException {
        try {
            if(r.nextInt(10) > valueAtThreshold) {
                value = function.valueAt(null);
                stop = value < 0;
//                consumer.notifyOf(this);
            }
            if(r.nextInt(10) > 7) {
                function.gradientAt(null);
            }
            if(r.nextInt(10) > 5) {
                function.hessianAt(null);
            }
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            throw new OptimizationException("error during optimization. cause: " + ex.getMessage());
        }
    }
    
    public Solution finish() {
        return new SingleSolution(Point.at(new double[] {3}), -2);
    }

    
    public StopCondition[] getStopConditions() {
        return new StopCondition[] { new StopCondition() {

            public boolean isConditionMet() {
                return stop;
            }
        }};
    }

    private Consumer<? super ValueTelemetry> consumer;

    public void addConsumer(Consumer<? super ValueTelemetry> consumer) {
        this.consumer = consumer;
    }

    public ValueTelemetry getValue() {
        return new ValueTelemetry(value);
    }
}

