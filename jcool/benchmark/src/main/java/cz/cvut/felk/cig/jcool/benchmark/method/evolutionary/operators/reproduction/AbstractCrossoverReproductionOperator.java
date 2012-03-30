package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.ReproductionOperator;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Property;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 23:08
 * Common predecessor of all crossover reproduction operators.
 */
public abstract class AbstractCrossoverReproductionOperator implements ReproductionOperator {

    @Property(name = "create both children", description = "determines whether supplement child will be created as well")
    protected boolean createBothChildren = true;

    ObjectiveFunction function;
    PopulationFactory populationFactory;
    RandomGenerator randomGenerator;
    int generationNumber = 0;

    public void checkConsistency(Population[] populations) throws OptimizationException{
        if (this.function == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": function has not been set");
        }
        if (this.randomGenerator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": randomGenerator has not been set");
        }
        if (this.populationFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": populationFactory has not been set");
        }
        if (populations == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] cannot be null");
        }
        if (populations.length < getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] has to contain at least " + getInputArity() + " populations");
        }
    }

    public void setFunction(ObjectiveFunction objectiveFunction) {
        this.function = objectiveFunction;
    }

    public int getInputArity() {
        return 2;
    }

    public int[] getResultsSizes(int[] parentPopulationSizes) {
        if (parentPopulationSizes == null || parentPopulationSizes.length < this.getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": parentPopulationSizes has to be non-null and at least size of " + this.getInputArity());
        }
        int retSize = Math.min(parentPopulationSizes[0], parentPopulationSizes[1]);
        return (createBothChildren)? new int[]{retSize, retSize} : new int[]{retSize};
    }

    public void setPopulationFactory(PopulationFactory populationFactory) {
        this.populationFactory = populationFactory;
    }

    public void setRandomGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public void resetGenerationCount() {
        this.generationNumber = 0;
    }

    public void nextGeneration() {
        this.generationNumber++;
    }

    public void setGeneration(int currentGeneration) {
        this.generationNumber = currentGeneration;
    }

    public boolean isCreateBothChildren() {
        return this.createBothChildren;
    }

    public void setCreateBothChildren(boolean createBothChildren) {
        this.createBothChildren = createBothChildren;
    }

    public int getOutputArity() {
        return this.createBothChildren ? 2 : 1;
    }
}
