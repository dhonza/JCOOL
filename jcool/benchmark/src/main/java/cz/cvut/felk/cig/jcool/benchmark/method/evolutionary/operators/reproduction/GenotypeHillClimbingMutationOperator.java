package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.IterationStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 31.3.2011
 * Time: 14:14
 * Genotype HillClimbing mutation reproduction operator suitable for niching Parallel HillClimbing optimization method.
 * Mutates genes from MSB to LSB. Mutated gene closes to LSB by one gene with one generation.
 */
@Component(name = "Genotype hill-climbing mutation reproduction operator", description = "mutation operator suitable for genotype variant of Parallel Hill-Climbing niching method")
public class GenotypeHillClimbingMutationOperator extends AbstractHillClimbingMutationOperator {

    protected IterationStopCondition stopCondition;

    public GenotypeHillClimbingMutationOperator() {
        this.stopCondition = new IterationStopCondition();
    }

    @Override
    protected void reproduceInternal(Individual[] children, Individual[] parents) {

        this.setStopCondition(children);

        Population temporaryPopulation = this.populationFactory.createPopulation(); // suites as carrier for child that is being evaluated for fitness
        Population[] temporaryPopulations = new Population[]{temporaryPopulation};

        // for every child
        for (int individualIdx = 0; individualIdx < children.length; individualIdx++){
            Individual child = children[individualIdx];
            // don't forget to set parent fitness!
            child.setParentFitness(parents[individualIdx].getFitness());
            temporaryPopulation.setIndividuals(new Individual[]{child}); // preparing to avoid multiple instantiation of Individual[1] per iteration of the same individual

            GenotypeRepresentation childRepresentation = (GenotypeRepresentation)children[individualIdx].getRepresentation();
            int numVariables = children[individualIdx].getRepresentation().getDoubleValue().length;
            int[] valueLengths = childRepresentation.getValueLengths();
            int[] accumulatedStarts = new int[numVariables];
            int[] accumulatedEnds = new int[numVariables];
            accumulatedStarts[0] = 0;
            accumulatedEnds[0] = valueLengths[0];
            for (int i = 1; i < numVariables; i++){
                accumulatedStarts[i] = accumulatedEnds[i-1];
                accumulatedEnds[i] = accumulatedEnds[i-1] + valueLengths[i];
            }


            // randomly pick a starting variable
            int currentVariable = this.randomGenerator.nextInt(numVariables);
            double oldChildFitness; // old value to compare improvement in repetitive mutation - usage of parent fitness is useless because in some cases the child can be always fitter that the parent and we will stuck in infinite loop
            double oldChildValue;
            boolean globalChange = true;
            boolean variableChanged;

            while (globalChange){
                globalChange = false;

                // for each variable
                for (int j = 0; j < numVariables; j++, currentVariable = (currentVariable + 1) % numVariables) {
                    oldChildFitness = child.getFitness();
                    oldChildValue = child.getValue();
                    variableChanged = false;
                    
                    // perform mutation only if not over-indexed to another variable
                    int geneIndex = accumulatedStarts[currentVariable] + this.generationNumber;
                    if (geneIndex < accumulatedEnds[currentVariable]){
                        childRepresentation.invertGeneAt(geneIndex); // manipulation with BinaryGenome is directly reflected into child's position
                        Point point = child.getCurrentPosition();
                        if ( this.function.inBounds(point) ){
                            child.setValue(this.function.valueAt(point));
                            // evaluate fitness
                            this.fitnessFunction.computeFitness(temporaryPopulations);

                            // if adding currentStepSize yields improved fitness
                            if (child.getFitness() > oldChildFitness){
                                globalChange = true;
                                variableChanged = true;
                            }
                        }
                        // if no variable change, then rollback last change
                        if (!variableChanged){
                            childRepresentation.invertGeneAt(geneIndex);
                            child.setFitness(oldChildFitness);
                            child.setValue(oldChildValue);
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets numIterations according to the longest variable genome in given Individual array.
     * @param children - children with
     */
    protected void setStopCondition(Individual[] children){
        int numIterations = 0;
        if (children.length > 0){
            GenotypeRepresentation representation = ((GenotypeRepresentation)children[0].getRepresentation());
            int[] lengths = representation.getValueLengths();
            for (int length : lengths){
                if (length > numIterations){
                    numIterations = length;
                }
            }
        }
        this.stopCondition.setNumIterations(numIterations);
    }

    public Class<? extends Representation> getAcceptableType() {
        return GenotypeRepresentation.class;
    }

    public StopCondition[] getStopConditions() {
        return new StopCondition[]{this.stopCondition};
    }

    @Override
    public void resetGenerationCount() {
        super.resetGenerationCount();

        this.stopCondition.init(10); // just to reset the met condition
    }

    @Override
    public void nextGeneration() {
        super.nextGeneration();

        this.stopCondition.nextIteration();
    }
}
