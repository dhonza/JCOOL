package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.fitness;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.FitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:18:52
 * SimpleFitnessFunction directly converts function value into fitness.
 * Optionally inverts function value if the wanted extreme is minimum, not the maximum.
 */
@Component(name = "Simple fitness function", description = "Fitness function that converts function value into fitness value with optional inversion or added offset")
public class SimpleFitnessFunction implements FitnessFunction{

    @Property(name = "invert function value", description = "if set to true then function value is inverted (positive made negative and vise versa). Invert when looking for minimal function value")
    protected boolean invertValue = false;

    @Property(name = "function value offset", description = "offset that is added to the function value after optional invert of original function value")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    protected double functionValueOffset = 0.0;

    @Property(name = "function value multiplier", description = "after addition of offset, the resultant value is multiplied to make linear extension/contradiction")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    protected double functionValueMultiplier = 1.0;

    @Property(name = "raise function value", description = "after linear stretching the value can be raised to given -th power to create non-linear extension/stretching")
    protected boolean raiseFunctionValue = false;

    @Property(name = "function value power", description = "the resultant value will be raised to given -th power")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    protected double functionValuePower = 1.1;

    public void computeFitness(Population[] populations) {
        Individual[] individuals;
        Individual individual;
        double fitness;
        for (Population population : populations) {
            individuals = population.getIndividuals();
            for (Individual individual1 : individuals) {
                individual = individual1;
                fitness = this.invertValue ? -individual.getValue() : individual.getValue();
                fitness += functionValueOffset;
                fitness *= this.functionValueMultiplier;
                if (this.raiseFunctionValue){
                    fitness = Math.pow(fitness, this.functionValuePower);
                }
                individual.setFitness(fitness);
            }
        }
    }

    public boolean isDependantOnPopulationComposition() {
        return false;
    }

    public boolean isDynamic() {
        return false;
    }

    public void resetGenerationCount() {
    }

    public void nextGeneration() {
    }

    public void setGeneration(int currentGeneration) {
    }

    public boolean isInvertValue() {
        return invertValue;
    }

    public void setInvertValue(boolean invertValue) {
        this.invertValue = invertValue;
    }

    public double getFunctionValueOffset() {
        return functionValueOffset;
    }

    public void setFunctionValueOffset(double functionValueOffset) {
        this.functionValueOffset = functionValueOffset;
    }

    public double getFunctionValueMultiplier() {
        return functionValueMultiplier;
    }

    public void setFunctionValueMultiplier(double functionValueMultiplier) {
        this.functionValueMultiplier = functionValueMultiplier;
    }

    public boolean isRaiseFunctionValue() {
        return raiseFunctionValue;
    }

    public void setRaiseFunctionValue(boolean raiseFunctionValue) {
        this.raiseFunctionValue = raiseFunctionValue;
    }

    public double getFunctionValuePower() {
        return functionValuePower;
    }

    public PropertyState getFunctionValuePowerState() {
        return this.raiseFunctionValue ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public void setFunctionValuePower(double functionValuePower) {
        this.functionValuePower = functionValuePower;
    }
}
