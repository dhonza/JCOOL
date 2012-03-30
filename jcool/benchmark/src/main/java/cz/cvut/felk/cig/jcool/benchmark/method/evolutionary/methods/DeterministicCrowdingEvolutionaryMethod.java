package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.methods;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.ReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.SelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.PhenotypeGaussianMutationReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.PhenotypeUniformCrossoverReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection.DeterministicCrowdingSurvivalSelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection.PermutationSelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;
import org.ytoh.configurations.ui.DynamicDropDown;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 30.3.2011
 * Time: 22:59
 * Deterministic crowding niching optimization method.
 */
@Component(name = "Deterministic crowding optimization method", description = "Method implementing niching deterministic crowding")
public class DeterministicCrowdingEvolutionaryMethod extends AbstractEvolutionaryMethod {

    @Property(name = "population size", description = "population size has to be dividable by 2")
    @Range(from = 1, to = Integer.MAX_VALUE)
    protected int populationSize = 100;

    @Property(name = "parent selection operator", description = "selection operator with input arity 1 and output arity 2")
    @DynamicDropDown(key = "selectionOperators", type = SelectionOperator.class, label = DynamicDropDown.Label.NAME)
    protected SelectionOperator parentSelectionOperator = new PermutationSelectionOperator();

    @Property(name = "crossover reproduction operator", description = "crossover reproduction operator with input and output arity 2")
    @DynamicDropDown(key = "reproductionOperators", type = ReproductionOperator.class, label = DynamicDropDown.Label.NAME)
    protected ReproductionOperator crossoverReproductionOperator = new PhenotypeUniformCrossoverReproductionOperator();

    @Property(name = "use mutation reproduction operator", description = "determines whether breeding pipeline will also use the specified mutatio reproduction operator")
    protected boolean useMutationReproductionOperator = true;

    @Property(name = "mutation reproduction operator", description = "additional mutation reproduction operator for both children")
    @DynamicDropDown(key = "reproductionOperators", type = ReproductionOperator.class, label = DynamicDropDown.Label.NAME)
    protected ReproductionOperator mutationReproductionOperator = new PhenotypeGaussianMutationReproductionOperator();

    @Property(name = "survival selection operator")
    @DynamicDropDown(key = "selectionOperators", type = SelectionOperator.class, label = DynamicDropDown.Label.NAME)
    protected SelectionOperator survivalSelectionOperator = new DeterministicCrowdingSurvivalSelectionOperator();

    protected SimpleStopCondition stopCondition;

    public DeterministicCrowdingEvolutionaryMethod(){
        this.stopCondition = new SimpleStopCondition();
        this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
        this.telemetry = new ValuePointListTelemetry();
    }

    public void optimize() {
        // evaluate function value and/or fitness values
        this.computeParentsValueAndFitness();
        // select parents
        Population[] selectedParents = this.selectParentPopulations();
        // breed
        Population[] children = this.crossoverReproductionOperator.reproduce(selectedParents);
        // optional secondary reproduction
        children = this.performSecondaryReproduction(children);

        // evaluate new individuals
        this.functionEvaluator.evaluate(children, this.function);
        // compute their fitness depending on function value and fitness function dynamics
        this.computeChildrenFitness(children);

        // choosing the best individual in parent as well as in children before some individuals will be left out
        Population[] candidatesForBestIndividual = PopulationUtils.appendPopulation(children, this.parentPopulation, true); // unification makes it more simple
        Individual bestIndividual = IndividualUtils.getBestIndividual(candidatesForBestIndividual);

        // join children and parents into single population
        Population[] competitorsForSurvival = new Population[]{children[0], children[1], selectedParents[0], selectedParents[1]};
        // select survivors
        this.parentPopulation = this.selectSurvivors(competitorsForSurvival)[0];

        // updating the solution
        if (bestIndividual.getValue() < this.solution.getValue()){
            this.solution = ValuePoint.at(Point.at(bestIndividual.getRepresentation().getDoubleValue()), bestIndividual.getValue());
        }

        stopCondition.setValue(solution.getValue());
        this.createTelemetry();
        this.notifyConsumer();

        // next time tick
        this.parentSelectionOperator.nextGeneration();
        this.crossoverReproductionOperator.nextGeneration();
        if (this.mutationReproductionOperator != null){
            this.mutationReproductionOperator.nextGeneration();
        }
        if (this.parentSelectionOperator != this.survivalSelectionOperator){
            this.survivalSelectionOperator.nextGeneration();
        }
        this.fitnessFunction.nextGeneration();
        this.function.nextGeneration();
    }

    public void validateConfiguration() throws OptimizationException {
        super.validateConfiguration();
        
        if (this.parentSelectionOperator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + " parent selection operator has not been set.");
        }
        if (this.crossoverReproductionOperator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + " crossover reproduction operator has not been set.");
        }
        if (this.crossoverReproductionOperator.getInputArity() != 2){
            throw new OptimizationException(this.getClass().getSimpleName() + ": given crossover reproduction operator has to have input arity 2, but given has " + this.crossoverReproductionOperator.getInputArity());
        }
        if (this.crossoverReproductionOperator.getOutputArity() != 2){
            throw new OptimizationException(this.getClass().getSimpleName() + ": given crossover reproduction operator has to have output arity 2, but given has " + this.crossoverReproductionOperator.getOutputArity());
        }
        if (this.useMutationReproductionOperator){
            this.checkMutationReproductionOperator();
        }
        if (this.survivalSelectionOperator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + " survival selection operator has not been set.");
        }
        if (populationSize % 2 != 0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": population size has to be dividable by 2, but " + populationSize + " is not");
        }
    }

    protected void checkMutationReproductionOperator(){
        if (this.mutationReproductionOperator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + " mutation reproduction operator has not been set.");
        }
        if (this.crossoverReproductionOperator == this.mutationReproductionOperator){
            throw new OptimizationException(this.getClass().getSimpleName() + ": secondary reproduction operator and primary reproduction operators cannot be the same");
        }
        if (this.mutationReproductionOperator.getInputArity() != 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": given mutation reproduction operator has to have input arity 1, but given has " + this.mutationReproductionOperator.getInputArity());
        }
        if (this.mutationReproductionOperator.getOutputArity() < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": given mutation reproduction operator has to have output arity at least 1, but given has " + this.mutationReproductionOperator.getOutputArity());
        }
    }

    public void init(ObjectiveFunction function){
        super.init(function);
        
        this.parentSelectionOperator.setPopulationFactory(this.populationFactory);
        this.parentSelectionOperator.setRandomGenerator(this.randomGenerator);
        this.configureParentSelectionOperator();

        this.crossoverReproductionOperator.setFunction(function);
        this.crossoverReproductionOperator.setPopulationFactory(this.populationFactory);
        this.crossoverReproductionOperator.setRandomGenerator(this.randomGenerator);

        if (this.mutationReproductionOperator != null){
            this.mutationReproductionOperator.setFunction(function);
            this.mutationReproductionOperator.setPopulationFactory(this.populationFactory);
            this.mutationReproductionOperator.setRandomGenerator(this.randomGenerator);
        }

        this.survivalSelectionOperator.setPopulationFactory(this.populationFactory);
        this.survivalSelectionOperator.setRandomGenerator(this.randomGenerator);
        this.configureSurvivalSelectionOperator();

        // reset time dependant operators
        this.parentSelectionOperator.resetGenerationCount();
        this.crossoverReproductionOperator.resetGenerationCount();
        if (this.mutationReproductionOperator != null){
            this.mutationReproductionOperator.resetGenerationCount();
        }
        if (this.parentSelectionOperator != this.survivalSelectionOperator){
            this.survivalSelectionOperator.resetGenerationCount();
        }
        this.fitnessFunction.resetGenerationCount();
        this.function.resetGenerationCount();

        // make initial population
        this.parentPopulation = this.populationFactory.createPopulation(this.individualFactory.createIndividuals(this.populationSize, 0));
        this.fitnessFunction.computeFitness(new Population[]{this.parentPopulation});
        Individual bestIndividual = this.parentPopulation.getBestIndividual();
        this.solution = ValuePoint.at(Point.at(bestIndividual.getRepresentation().getDoubleValue()), bestIndividual.getValue());

        this.stopCondition.setInitialValue(solution.getValue());
    }

    /**
     * Configures arity and individualsPerPopulation of parentSelectionOperator
     */
    protected void configureParentSelectionOperator(){
        this.parentSelectionOperator.setInputArity(1);
        this.parentSelectionOperator.setOutputArity(2);
        this.parentSelectionOperator.setIndividualsPerPopulation(this.populationSize / 2);
    }

    /**
     * Configures arity and individualsPerPopulation of survivalSelectionOperator
     */
    protected void configureSurvivalSelectionOperator(){
        this.survivalSelectionOperator.setInputArity(4);
        this.survivalSelectionOperator.setOutputArity(1);
        this.survivalSelectionOperator.setIndividualsPerPopulation(populationSize);
    }

    /**
     * If secondary reproduction is required, then performs reproduction of each population in children populations and the output will assemble in output array of population.
     * Children populations mean populations, that were made by the primary reproduction operator.
     * @param children - children for secondary reproduction
     * @return populations of individuals that hat been reproduced with secondary reproduction operator.
     */
    protected Population[] performSecondaryReproduction(Population[] children){
        if (this.useMutationReproductionOperator){
            this.checkMutationReproductionOperator();
            // evaluate individuals for correct parentFitness values
            this.functionEvaluator.evaluate(children, this.function);
            this.computeChildrenFitness(children);
            // proceed to breed
            Population[] outputPopulations = new Population[children.length];
            for (int i = 0; i < outputPopulations.length; i++){
                outputPopulations[i] = this.mutationReproductionOperator.reproduce(new Population[]{children[i]})[0];
            }
            return outputPopulations;
        } else {
            return children;
        }
    }

    /**
     * Configures parentSelectionOperator and performs parent selection.
     * @return Population[] of selected parents.
     */
    protected Population[] selectParentPopulations(){
        this.configureParentSelectionOperator();
        return this.parentSelectionOperator.select(new Population[]{this.parentPopulation});
    }

    /**
     * Configures survivalSelectionOperator and performs survival selection.
     * @param competitorsForSurvival - populations which individuals will compete for survival.
     * @return Population[] of selected survivors.
     */
    protected Population[] selectSurvivors(Population[] competitorsForSurvival){
        this.configureSurvivalSelectionOperator();
        return this.survivalSelectionOperator.select(competitorsForSurvival);
    }

    public StopCondition[] getStopConditions() {
        return new StopCondition[]{this.stopCondition};
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public SelectionOperator getParentSelectionOperator() {
        return parentSelectionOperator;
    }

    public void setParentSelectionOperator(SelectionOperator parentSelectionOperator) {
        this.parentSelectionOperator = parentSelectionOperator;
    }

    public ReproductionOperator getCrossoverReproductionOperator() {
        return crossoverReproductionOperator;
    }

    public void setCrossoverReproductionOperator(ReproductionOperator crossoverReproductionOperator) {
        this.crossoverReproductionOperator = crossoverReproductionOperator;
    }

    public boolean isUseMutationReproductionOperator() {
        return useMutationReproductionOperator;
    }

    public void setUseMutationReproductionOperator(boolean useMutationReproductionOperator) {
        this.useMutationReproductionOperator = useMutationReproductionOperator;
    }

    public ReproductionOperator getMutationReproductionOperator() {
        return mutationReproductionOperator;
    }

    public PropertyState getMutationReproductionOperatorState() {
        return this.useMutationReproductionOperator ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public void setMutationReproductionOperator(ReproductionOperator mutationReproductionOperator) {
        this.mutationReproductionOperator = mutationReproductionOperator;
    }

    public SelectionOperator getSurvivalSelectionOperator() {
        return survivalSelectionOperator;
    }

    public void setSurvivalSelectionOperator(SelectionOperator survivalSelectionOperator) {
        this.survivalSelectionOperator = survivalSelectionOperator;
    }
}
