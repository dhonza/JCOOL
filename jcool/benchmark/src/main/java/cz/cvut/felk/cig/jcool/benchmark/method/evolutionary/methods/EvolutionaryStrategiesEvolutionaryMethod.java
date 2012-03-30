package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.methods;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.ReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.SelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.PhenotypeGaussianMutationReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection.TruncationSelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection.UniformDeterministicSelectionOperator;
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
 * Date: 4.4.2011
 * Time: 21:00
 * Implementation of Evolutionary strategies optimization method.
 * Provides overlapping as well as non-overlapping generational model. Also provides steady-state and batch(generational) model.
 */
@Component(name = "Evolutionary strategies evolutionary method", description = "Method providing all basic variants of evolutionary strategies")
public class EvolutionaryStrategiesEvolutionaryMethod extends AbstractEvolutionaryMethod {

    @Property(name = "Description of configuration", description = "String description of resulting configuration of method")
    protected String description;

    @Property(name = "parent population size", description = "sets size of population at the beginning of each generation")
    @Range(from = 1, to = Integer.MAX_VALUE)
    protected int populationSize = 1;

    @Property(name = "children to parent ratio", description = "how many children will produce each parent")
    @Range(from = 1, to = Integer.MAX_VALUE)
    protected int childrenToParentRatio = 10;

    @Property(name = "overlapping populations", description = "if set to true then parents will complete with children for survival")
    protected boolean useOverlappingGenerations = false;

    @Property(name = "steady state population", description = "if set to true then each child will immediately compete for survival with parents")
    protected boolean useSteadyStatePopulation = false;

    @Property(name = "parent selection operator", description = "selection operator used for selection of single parent in case of overlapping populations or for multiple selection of the same parent in case of traditional evolutionary strategy method")
    @DynamicDropDown(key = "selectionOperators", type = SelectionOperator.class, label = DynamicDropDown.Label.NAME)
    protected SelectionOperator parentSelectionOperator = new UniformDeterministicSelectionOperator();

    @Property(name = "mutation operator", description = "mutation operator used to introduce variability in children")
    @DynamicDropDown(key = "mutationReproductionOperators", type = ReproductionOperator.class, label = DynamicDropDown.Label.NAME)
    protected ReproductionOperator mutationOperator = new PhenotypeGaussianMutationReproductionOperator();

    @Property(name = "survival selection operator", description = "operator used for selection of individuals proceeding to next generation")
    @DynamicDropDown(key = "selectionOperators", type = SelectionOperator.class, label = DynamicDropDown.Label.NAME)
    protected SelectionOperator survivalSelectionOperator = new TruncationSelectionOperator();

    protected SimpleStopCondition stopCondition;

    public EvolutionaryStrategiesEvolutionaryMethod() {
        this.stopCondition = new SimpleStopCondition();
        this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
        this.telemetry = new ValuePointListTelemetry();
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public void setPopulationSize(int size) {
        this.populationSize = size;
        this.refreshDescription();
    }

    public void validateConfiguration() throws OptimizationException {
        super.validateConfiguration();
        if (this.populationSize < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": population size cannot be smaller than one, but " + this.populationSize + " is smaller");
        }
        if (this.childrenToParentRatio < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": children to parent ratio cannot be smaller than one, but " + this.childrenToParentRatio + " is smaller");
        }
        if (!this.mutationOperator.getAcceptableType().isAssignableFrom(this.representationFactory.getRepresentationType())){
            throw new OptimizationException(this.getClass().getSimpleName() + ": reproduction operator does not match with representation.");
        }
        if (this.mutationOperator.getInputArity() != 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": mutation operator has to have input arity of 1, but the given has " + this.mutationOperator.getInputArity());
        }
        if (this.mutationOperator.getOutputArity() < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": mutation operator has to have output arity at least 1, but the given has " + this.mutationOperator.getOutputArity());
        }
    }

    public StopCondition[] getStopConditions() {
        return new StopCondition[]{this.stopCondition};
    }

    public void optimize() {
        // evaluate function value and/or fitness values
        this.computeParentsValueAndFitness();
        // select parents
        Population[] selectedParents = this.selectParentPopulations();
        // breed
        Population[] children = this.mutationOperator.reproduce(selectedParents);
        // evaluate new individuals
        this.functionEvaluator.evaluate(children, this.function);
        // compute their fitness depending on fitness function type
        this.computeChildrenFitness(children);

        // choosing the best individual in parent as well as in children before some individuals will be left out
        Population[] candidatesForBestIndividual = PopulationUtils.appendPopulation(children, this.parentPopulation, true); // unification makes it more simple
        Individual bestIndividual = IndividualUtils.getBestIndividual(candidatesForBestIndividual);

        // survival selection
        Population[] competitorsForSurvival;
        if (this.useOverlappingGenerations){
            competitorsForSurvival = PopulationUtils.appendPopulation(children, this.parentPopulation, true);
        } else {
            competitorsForSurvival = children;
        }
        this.parentPopulation = this.selectSurvivors(competitorsForSurvival)[0];

        // updating the solution
        if (bestIndividual.getValue() < this.solution.getValue()){
            this.solution = ValuePoint.at(Point.at(bestIndividual.getRepresentation().getDoubleValue()), bestIndividual.getValue());
        }

//        System.out.println("best value: " + bestIndividual.getValue());
//        System.out.println("solution value: " + solution.getValue());
//        System.out.println();

        stopCondition.setValue(solution.getValue());
        this.createTelemetry();
        this.notifyConsumer();

        // next time tick
        this.parentSelectionOperator.nextGeneration();
        this.mutationOperator.nextGeneration();
        if (this.parentSelectionOperator != this.survivalSelectionOperator){
            this.survivalSelectionOperator.nextGeneration();
        }
        this.fitnessFunction.nextGeneration();
        this.function.nextGeneration();
    }

    @Override
    public void init(ObjectiveFunction function) {
        super.init(function);

        this.parentSelectionOperator.setPopulationFactory(this.populationFactory);
        this.parentSelectionOperator.setRandomGenerator(this.randomGenerator);
        this.configureParentSelectionOperator();
        this.mutationOperator.setFunction(this.function);
        this.mutationOperator.setPopulationFactory(this.populationFactory);
        this.mutationOperator.setRandomGenerator(this.randomGenerator);
        this.survivalSelectionOperator.setRandomGenerator(this.randomGenerator);
        this.survivalSelectionOperator.setPopulationFactory(this.populationFactory);
        this.configureSurvivalSelectionOperator();

        // reset time dependant operators
        this.parentSelectionOperator.resetGenerationCount();
        this.mutationOperator.resetGenerationCount();
        this.survivalSelectionOperator.resetGenerationCount();
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
        this.parentSelectionOperator.setOutputArity(this.mutationOperator.getInputArity());
        int individualsPerPopulation = this.useSteadyStatePopulation ? 1 : this.populationSize * this.childrenToParentRatio;
        this.parentSelectionOperator.setIndividualsPerPopulation(individualsPerPopulation);
    }

    /**
     * Configures arity and individuals per population of survivalSelectionOperator
     */
    protected void configureSurvivalSelectionOperator(){
        this.survivalSelectionOperator.setIndividualsPerPopulation(this.populationSize);
        this.survivalSelectionOperator.setInputArity(this.mutationOperator.getOutputArity() + (this.useOverlappingGenerations ? 1 : 0));
        this.survivalSelectionOperator.setOutputArity(1);
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
     * @return Population[] of selected survivors.
     */
    protected Population[] selectSurvivors(Population[] competitorsForSurvival){
        this.configureSurvivalSelectionOperator();
        return survivalSelectionOperator.select(competitorsForSurvival);
    }

    public String getDescription() {
        return description;
    }

    public PropertyState getDescriptionState() {
        return PropertyState.DISABLED;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected void refreshDescription(){
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(this.populationSize);
        if (this.useOverlappingGenerations){
            sb.append(" + ");
        } else {
            sb.append(", ");
        }
        if (this.useSteadyStatePopulation){
            sb.append("1");
        } else {
            sb.append(this.populationSize * this.childrenToParentRatio);
        }
        sb.append(")");
        this.description = sb.toString();
    }

    public int getChildrenToParentRatio() {
        return childrenToParentRatio;
    }

    // available only if not steady state
    public PropertyState getChildrenToParentRatioState() {
        return this.useSteadyStatePopulation ? PropertyState.DISABLED : PropertyState.ENABLED;
    }

    public void setChildrenToParentRatio(int childrenToParentRatio) {
        this.childrenToParentRatio = childrenToParentRatio;
        this.refreshDescription();
    }

    public boolean getUseOverlappingGenerations() {
        return useOverlappingGenerations;
    }

    public PropertyState getUseOverlappingGenerationsState() {
        return this.useSteadyStatePopulation ? PropertyState.DISABLED : PropertyState.ENABLED;
    }

    public void setUseOverlappingGenerations(boolean useOverlappingGenerations) {
        this.useOverlappingGenerations = useOverlappingGenerations;
        this.refreshDescription();
    }

    public boolean isUseSteadyStatePopulation() {
        return useSteadyStatePopulation;
    }

    public void setUseSteadyStatePopulation(boolean useSteadyStatePopulation) {
        if (useSteadyStatePopulation){
            this.useOverlappingGenerations = true;
        }
        this.useSteadyStatePopulation = useSteadyStatePopulation;
        this.refreshDescription();
    }

    public SelectionOperator getParentSelectionOperator() {
        return parentSelectionOperator;
    }

    public void setParentSelectionOperator(SelectionOperator parentSelectionOperator) {
        this.parentSelectionOperator = parentSelectionOperator;
    }

    public ReproductionOperator getMutationOperator() {
        return mutationOperator;
    }

    public void setMutationOperator(ReproductionOperator mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    public SelectionOperator getSurvivalSelectionOperator() {
        return survivalSelectionOperator;
    }

    public void setSurvivalSelectionOperator(SelectionOperator survivalSelectionOperator) {
        this.survivalSelectionOperator = survivalSelectionOperator;
    }
}
