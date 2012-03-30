package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.EpsilonStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 31.3.2011
 * Time: 14:13
 * Cannot work with fitness that is dependant on populations composition
 */
@Component(name = "Phenotype hill-climbing mutation reproduction operator", description = "mutation operator suitable for phenotype variant of Parallel Hill-Climbing niching method. The step size is half-size in every next generation")
public class PhenotypeHillClimbingMutationOperator extends AbstractHillClimbingMutationOperator {

    @Property(name = "initial step size", description = "initial step size that will be added or subtracted from every variable during mutation")
    @Range(from = 0.0, to = Double.MAX_VALUE)
    protected double initialStepSize = 1.0;

    @Property(name = "final step size", description = "value of step size after which the mutation will stop")
    @Range(from = 0.0, to = Double.MAX_VALUE)
    protected double finalStepSize = 0.01;

    protected double currentStepSize;

    protected EpsilonStopCondition stopCondition;
    protected boolean stopConditionSet;

    public PhenotypeHillClimbingMutationOperator(){
        super();
        this.stopCondition = new EpsilonStopCondition();
        this.stopCondition.init(this.finalStepSize, MachineAccuracy.EPSILON);
        this.stopConditionSet = false;
        this.currentStepSize = this.initialStepSize;
    }

    public void checkConsistency(Population[] populations) throws OptimizationException{
        super.checkConsistency(populations);
        
        if (this.initialStepSize < 0.0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": initial step size cannot be smaller that zero, but given value " + this.initialStepSize + " is");
        }
        if (this.finalStepSize < 0.0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": final step size cannot be smaller that zero, but given value " + this.finalStepSize + " is");
        }
        if (this.initialStepSize <= this.finalStepSize){
            throw new OptimizationException(this.getClass().getSimpleName() + ": initial step size has to be bigger that final step size, but " + this.initialStepSize + " <= " + this.finalStepSize);
        }
    }

    protected void reproduceInternal(Individual[] children, Individual[] parents) {
        
        Population temporaryPopulation = this.populationFactory.createPopulation(); // suites as carrier for child that is being evaluated for fitness

        // for every child
        for (int individualIdx = 0; individualIdx < children.length; individualIdx++){
            // don't forget to set parent fitness!
            children[individualIdx].setParentFitness(parents[individualIdx].getFitness());

            Individual child = children[individualIdx];
            Representation childRepresentation = children[individualIdx].getRepresentation();
            double[] childValues = children[individualIdx].getRepresentation().getDoubleValue();
            // randomly pick a starting variable
            int currentVariable = this.randomGenerator.nextInt(childValues.length);
            double oldVariableValue; // backup of original child variable before each iteration
            double oldChildFitness; // old value to compare improvement in repetitive mutation - usage of parent fitness is useless because in some cases the child can be always fitter that the parent and we will stuck in infinite loop
            double oldChildValue;
            boolean globalChange = true;
            boolean variableChanged;

            while (globalChange){
                globalChange = false;

                // for each variable
                for (int j = 0; j < childValues.length; j++, currentVariable = (currentVariable + 1) % childValues.length) {

                    //backup old value
                    oldVariableValue = childValues[currentVariable];
                    oldChildFitness = child.getFitness();
                    oldChildValue = child.getValue();
                    variableChanged = false;
                    
                    // try addition first
                    childValues[currentVariable] += this.currentStepSize;
                    Point point = Point.at(childValues);
                    if (this.function.inBounds(point)){
                        childRepresentation.setDoubleValue(childValues); // set double value so that fitness will be computed from new position
                        // evaluate function value
                        child.setValue(this.function.valueAt(point));
                        temporaryPopulation.setIndividuals(new Individual[]{child});
                        // evaluate fitness
                        this.fitnessFunction.computeFitness(new Population[]{temporaryPopulation});

                        // if adding currentStepSize yields improved fitness
                        if (child.getFitness() > oldChildFitness){
                            globalChange = true;
                            variableChanged = true;
                        }
                    }

                    // if no globalChange due to inefficient addition or due to out of bounds, then try subtraction
                    if (!variableChanged){
                        // try rollback with subtraction
                        childValues[currentVariable] = oldVariableValue - this.currentStepSize;
                        point = Point.at(childValues);
                        if (this.function.inBounds(point)){
                            childRepresentation.setDoubleValue(childValues); // set double value so that fitness will be computed from new position
                            // evaluate function value
                            child.setValue(this.function.valueAt(point));
                            temporaryPopulation.setIndividuals(new Individual[]{child}); // have to set it again because we don't know if previous step failed at the phase of bounds or fitness value
                            // evaluate fitness
                            this.fitnessFunction.computeFitness(new Population[]{temporaryPopulation});

                            // if subtracting currentStepSize yields improved fitness
                            if (child.getFitness() > oldChildFitness){
                                globalChange = true;
                                variableChanged = true;
                            }
                        }
                    }
                    // if still no globalChange, then rollback globalChange
                    if (!variableChanged){
                        childValues[currentVariable] = oldVariableValue;
                        child.getRepresentation().setDoubleValue(childValues);
                        child.setFitness(oldChildFitness);
                        child.setValue(oldChildValue);
                    }
                }
            }
        }

        // don't forget to update stop condition!
        this.stopCondition.setValue(this.currentStepSize);
    }

    public StopCondition[] getStopConditions() {
        return new StopCondition[]{this.stopCondition};
    }

    public Class<? extends Representation> getAcceptableType() {
        return PhenotypeRepresentation.class;
    }

    public void resetGenerationCount() {
        super.resetGenerationCount();

        this.stopCondition.init();
        this.currentStepSize = this.initialStepSize;
        this.stopCondition.init();
    }

    public void nextGeneration() {
        super.nextGeneration();
        
        this.currentStepSize /= 2;
    }

    public void setGeneration(int currentGeneration) {
        super.setGeneration(currentGeneration);

        if (currentGeneration == 0){
            this.currentStepSize = initialStepSize;
        } else {
            // try to artificially compute the stepSize for given generation
            this.currentStepSize = this.initialStepSize / Math.pow(2, currentGeneration);
        }
    }

    public double getInitialStepSize() {
        return initialStepSize;
    }

    public void setInitialStepSize(double initialStepSize) {
        this.initialStepSize = initialStepSize;
    }

    public double getFinalStepSize() {
        return finalStepSize;
    }

    public void setFinalStepSize(double finalStepSize) {
        this.finalStepSize = finalStepSize;
        this.stopCondition.setDesiredValue(finalStepSize);
    }
}
