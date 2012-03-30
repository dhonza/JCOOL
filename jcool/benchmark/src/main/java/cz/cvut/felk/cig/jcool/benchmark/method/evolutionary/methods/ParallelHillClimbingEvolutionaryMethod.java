package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.methods;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.HillClimbingMutationOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.PhenotypeHillClimbingMutationOperator;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;
import org.ytoh.configurations.ui.DynamicDropDown;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 31.3.2011
 * Time: 13:54
 * Implementation of ParallelHillClimbingEvolutionaryMethod that uses special time dependant reproduction operator = HillClimbingMutationOperator.
 * The implicit selection is truncation. The survivors are the results of mutation because result are at least that fit as their parents.
 */
@Component(name = "Parallel HillClimbing evolutionary method", description = "Implementation of parallel hillClimbing niching method")
public class ParallelHillClimbingEvolutionaryMethod extends AbstractEvolutionaryMethod {

    @Property(name = "population size")
    @Range(from = 1, to = Integer.MAX_VALUE)
    protected int populationSize = 10;

    @Property(name = "hillClimbing mutation operator", description = "Mutation operator that mutates all individuals in population and accepts only improvements")
    @DynamicDropDown(key = "hillClimbingMutationOperators", type = HillClimbingMutationOperator.class, label = DynamicDropDown.Label.NAME)
    protected HillClimbingMutationOperator mutationOperator = new PhenotypeHillClimbingMutationOperator();

    public ParallelHillClimbingEvolutionaryMethod(){
        this.telemetry = new ValuePointListTelemetry();
    }

    public void validateConfiguration() throws OptimizationException {
        super.validateConfiguration();

        if (this.mutationOperator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": random generator has not been set");
        }
        if (!this.mutationOperator.getAcceptableType().isAssignableFrom(this.representationFactory.getRepresentationType())){
            throw new OptimizationException(this.getClass().getSimpleName() + ": mutation operator does not match with representation.");
        }
        if (this.mutationOperator.getInputArity() != 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": mutation operator has to have input arity of one, but the given has " + this.mutationOperator.getInputArity());
        }
        if (this.mutationOperator.getOutputArity() != 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": mutation operator has to have output arity of one, but the given has " + this.mutationOperator.getOutputArity());
        }
    }

    public StopCondition[] getStopConditions() {
        if (this.mutationOperator == null){
            return new StopCondition[0];
        } else {
            return this.mutationOperator.getStopConditions();
        }
    }

    public void optimize() {
        // initial values and fitness are computed in init method, next values are computed directly during mutation
        this.parentPopulation = this.mutationOperator.reproduce(new Population[]{parentPopulation})[0];

        Individual bestIndividual = IndividualUtils.getBestIndividual(new Population[]{parentPopulation});

        // updating the solution
        if (bestIndividual.getValue() < this.solution.getValue()){
            this.solution = ValuePoint.at(bestIndividual.getCurrentPosition(), bestIndividual.getValue());
        }

        this.createTelemetry();
        this.notifyConsumer();

        // next time tick
        this.mutationOperator.nextGeneration();
        this.fitnessFunction.nextGeneration();
        this.function.nextGeneration();
    }

    @Override
    public void init(ObjectiveFunction function) {
        super.init(function);

        // set the only operator
        this.mutationOperator.setFunction(this.function);
        this.mutationOperator.setPopulationFactory(this.populationFactory);
        this.mutationOperator.setRandomGenerator(this.randomGenerator);
        this.mutationOperator.setFitnessFunction(this.fitnessFunction);

        mutationOperator.resetGenerationCount();
        this.fitnessFunction.resetGenerationCount();
        this.function.resetGenerationCount();

        // make initial population
        this.parentPopulation = this.populationFactory.createPopulation(this.individualFactory.createIndividuals(this.populationSize, 0));
        this.fitnessFunction.computeFitness(new Population[]{this.parentPopulation});
        Individual bestIndividual = this.parentPopulation.getBestIndividual();
        this.solution = ValuePoint.at(Point.at(bestIndividual.getRepresentation().getDoubleValue()), bestIndividual.getValue());
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public void setPopulationSize(int size) {
        this.populationSize = size;
    }

    public HillClimbingMutationOperator getMutationOperator() {
        return mutationOperator;
    }

    public void setMutationOperator(HillClimbingMutationOperator mutationOperator) {
        this.mutationOperator = mutationOperator;
    }
}
