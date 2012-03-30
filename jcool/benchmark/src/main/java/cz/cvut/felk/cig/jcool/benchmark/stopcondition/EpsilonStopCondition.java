package cz.cvut.felk.cig.jcool.benchmark.stopcondition;

import cz.cvut.felk.cig.jcool.core.StopCondition;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 16.4.2011
 * Time: 10:58
 * Stop condition that watches some variable and evaluates, if that variable is close enough to the desired value.
 * Used as stop condition when watched variable is decrementing in every step.
 */
@Component(name = "Epsilon stop condition", description = "Stop condition that watches some variable and evaluates, whether its value reached desired value with given accuracy")
public class EpsilonStopCondition implements StopCondition{

    protected double desiredValue;
    protected double actualValue;
    protected double accuracyEpsilon;
    protected boolean isMet;

    public void init(double desiredValue, double accuracyEpsilon) {
        this.desiredValue = desiredValue;
        this.accuracyEpsilon = accuracyEpsilon;
        this.isMet = false;
    }

    public void init(){
        this.isMet = false;
    }

    /**
     * Changes desired value without setting the condition met to false.
     * @param desiredValue - new desired value
     */
    public void setDesiredValue(double desiredValue){
        this.desiredValue = desiredValue;
    }

    public void setValue(double value) {
        this.actualValue = value;
        this.isMet = Math.abs(this.actualValue) <= ( Math.abs(this.desiredValue) + this.accuracyEpsilon );
    }

    public boolean isConditionMet() {
        return this.isMet;
    }

    @Override
    public String toString() {
        return "Variable reached desired surroundings with value " + this.actualValue;
    }
}
