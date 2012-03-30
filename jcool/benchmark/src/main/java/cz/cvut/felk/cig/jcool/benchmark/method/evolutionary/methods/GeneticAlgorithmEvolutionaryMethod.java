package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.methods;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.ReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.SelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.PhenotypeGaussianMutationReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.PhenotypeUniformCrossoverReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection.BinaryTournamentSelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection.UniformStochasticSelectionOperator;
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
 * User: SuperLooser
 * Date: 7.2.2011
 * Time: 15:15:37
 * Implementation of traditional GA with two reproduction operators. Second one is optional, meant to be used when the first one is crossover and user wants to add extra genes with mutation.
 */
@Component(name = "Genetic algorithm evolutionary method", description = "Implementation of genetic algorithm with at least one reproduction operator and one optional operator")
public class GeneticAlgorithmEvolutionaryMethod extends AbstractEvolutionaryMethod {

    @Property(name = "population size")
    @Range(from = 1, to = Integer.MAX_VALUE)
    protected int populationSize = 10;

    @Property(name = "overlapping parent population", description = "whether parent population should compete with offspring of die out")
    protected boolean overlappingPopulation = false;

    @Property(name = "parent selection operator")
    @DynamicDropDown(key = "selectionOperators", type = SelectionOperator.class, label = DynamicDropDown.Label.NAME)
    protected SelectionOperator parentSelectionOperator = new UniformStochasticSelectionOperator();

    @Property(name = "primary reproduction operator")
    @DynamicDropDown(key = "reproductionOperators", type = ReproductionOperator.class, label = DynamicDropDown.Label.NAME)
    protected ReproductionOperator reproductionOperator = new PhenotypeUniformCrossoverReproductionOperator();

    @Property(name = "use secondary reproduction operator", description = "if set to true then secondary reproduction operator will be used")
    protected boolean useSecondaryReproductionOperator = true;

    @Property(name = "secondary reproduction operator", description = "optional secondary reproduction operator applied on every population produced by primary reproduction operator. Required arities are one to one.")
    @DynamicDropDown(key = "reproductionOperators", type = ReproductionOperator.class, label = DynamicDropDown.Label.NAME)
    protected ReproductionOperator secondaryReproductionOperator = new PhenotypeGaussianMutationReproductionOperator();

    @Property(name = "survival selection operator")
    @DynamicDropDown(key = "selectionOperators", type = SelectionOperator.class, label = DynamicDropDown.Label.NAME)
    protected SelectionOperator survivalSelectionOperator = new BinaryTournamentSelectionOperator();

    protected SimpleStopCondition stopCondition;

    public GeneticAlgorithmEvolutionaryMethod(){
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
        Population[] children = this.reproductionOperator.reproduce(selectedParents);
        // optional secondary reproduction
        children = this.performSecondaryReproduction(children);

        // evaluate new individuals
        this.functionEvaluator.evaluate(children, this.function);
        // compute their fitness depending on function value and fitness function dynamics
        this.computeChildrenFitness(children);

        // choosing the best individual in parent as well as in children before some individuals will be left out
        Population[] candidatesForBestIndividual = PopulationUtils.appendPopulation(children, this.parentPopulation, true); // unification makes it more simple
        Individual bestIndividual = IndividualUtils.getBestIndividual(candidatesForBestIndividual);

        // survival selection
        Population[] competitorsForSurvival;
        if (this.overlappingPopulation){
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
        this.reproductionOperator.nextGeneration();
        if (this.secondaryReproductionOperator != null){
            this.secondaryReproductionOperator.nextGeneration();
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
            throw new OptimizationException(this.getClass().getSimpleName() + ": parent selection operator has to be set");
        }
        if (this.reproductionOperator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": reproduction operator has to be set");
        }
        if (this.survivalSelectionOperator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": survival selection operator has not been set");
        }
        if (!this.reproductionOperator.getAcceptableType().isAssignableFrom(this.representationFactory.getRepresentationType())){
            throw new OptimizationException(this.getClass().getSimpleName() + ": reproduction operator does not match with representation.");
        }
        if (this.useSecondaryReproductionOperator){
            this.checkSecondaryReproductionOperator();
        }
    }

    public void init(ObjectiveFunction function) {
        super.init(function);

        // set bindings between operators - arity and individuals per population
        this.parentSelectionOperator.setRandomGenerator(this.randomGenerator);
        this.parentSelectionOperator.setPopulationFactory(this.populationFactory);
        this.configureParentSelectionOperator();
        this.reproductionOperator.setFunction(this.function);
        this.reproductionOperator.setPopulationFactory(this.populationFactory);
        this.reproductionOperator.setRandomGenerator(this.randomGenerator);
        if (this.secondaryReproductionOperator != null){
            this.secondaryReproductionOperator.setFunction(function);
            this.secondaryReproductionOperator.setPopulationFactory(this.populationFactory);
            this.secondaryReproductionOperator.setRandomGenerator(this.randomGenerator);
        }
        this.survivalSelectionOperator.setRandomGenerator(this.randomGenerator);
        this.survivalSelectionOperator.setPopulationFactory(this.populationFactory);
        this.configureSurvivalSelectionOperator();

        // reset time dependant operators
        this.parentSelectionOperator.resetGenerationCount();
        this.reproductionOperator.resetGenerationCount();
        if (this.secondaryReproductionOperator != null){
            this.secondaryReproductionOperator.resetGenerationCount();
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

    public StopCondition[] getStopConditions() {
        return new StopCondition[]{this.stopCondition};
    }

    /**
     * Checks presence and correct arity of secondaryReproductionOperator.
     */
    protected void checkSecondaryReproductionOperator(){
        if (this.secondaryReproductionOperator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": secondary reproduction operator has not been set");
        }
        if (this.reproductionOperator == this.secondaryReproductionOperator){
            throw new OptimizationException(this.getClass().getSimpleName() + ": secondary reproduction operator and primary reproduction operators cannot be the same");
        }
        if (this.secondaryReproductionOperator.getInputArity() != 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": secondary reproduction operator has to have input arity of 1, but the given has " + this.secondaryReproductionOperator.getInputArity());
        }
        if (this.secondaryReproductionOperator.getOutputArity() < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": secondary reproduction operator has to have output arity at least, but the given has " + this.secondaryReproductionOperator.getOutputArity());
        }
    }

    /**
     * Configures arity and individualsPerPopulation of parentSelectionOperator
     */
    protected void configureParentSelectionOperator(){
        this.parentSelectionOperator.setIndividualsPerPopulation(this.populationSize);
        this.parentSelectionOperator.setInputArity(1);
        this.parentSelectionOperator.setOutputArity(this.reproductionOperator.getInputArity());
    }

    /**
     * Configures arity and individualsPerPopulation of survivalSelectionOperator
     */
    protected void configureSurvivalSelectionOperator(){
        this.survivalSelectionOperator.setIndividualsPerPopulation(this.populationSize);
        this.survivalSelectionOperator.setInputArity(this.reproductionOperator.getOutputArity() + (this.overlappingPopulation ? 1 : 0));
        this.survivalSelectionOperator.setOutputArity(1);
    }

    /**
     * If secondary reproduction is required, then performs reproduction of each population in children populations and the output will assemble in output array of population.
     * Children populations mean populations, that were made by the primary reproduction operator.
     * @param children - children for secondary reproduction
     * @return populations of individuals that hat been reproduced with secondary reproduction operator.
     */
    protected Population[] performSecondaryReproduction(Population[] children){
        if (this.useSecondaryReproductionOperator){
            this.checkSecondaryReproductionOperator();
            // evaluate individuals for correct parentFitness values
            this.functionEvaluator.evaluate(children, this.function);
            this.computeChildrenFitness(children);
            // proceed to breed
            Population[] outputPopulations = new Population[children.length];
            for (int i = 0; i < outputPopulations.length; i++){
                outputPopulations[i] = this.secondaryReproductionOperator.reproduce(new Population[]{children[i]})[0];
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
     * @return Population[] of selected survivors.
     */
    protected Population[] selectSurvivors(Population[] competitorsForSurvival){
        this.configureSurvivalSelectionOperator();
        return survivalSelectionOperator.select(competitorsForSurvival);
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public void setPopulationSize(int size) {
        this.populationSize = size;
    }

    public SelectionOperator getParentSelectionOperator() {
        return parentSelectionOperator;
    }

    public ReproductionOperator getReproductionOperator() {
        return reproductionOperator;
    }

    public SelectionOperator getSurvivalSelectionOperator() {
        return survivalSelectionOperator;
    }

    public void setParentSelectionOperator(SelectionOperator parentSelectionOperator) {
        this.parentSelectionOperator = parentSelectionOperator;
    }

    public void setParentPopulation(Population parentPopulation) {
        this.parentPopulation = parentPopulation;
    }

    public void setReproductionOperator(ReproductionOperator reproductionOperator) {
        this.reproductionOperator = reproductionOperator;
    }

    public void setSurvivalSelectionOperator(SelectionOperator survivalSelectionOperator) {
        this.survivalSelectionOperator = survivalSelectionOperator;
    }

    public boolean isOverlappingPopulation() {
        return overlappingPopulation;
    }

    public void setOverlappingPopulation(boolean overlappingPopulation) {
        this.overlappingPopulation = overlappingPopulation;
    }

    public ReproductionOperator getSecondaryReproductionOperator() {
        return secondaryReproductionOperator;
    }

    // disabled when !useSecondaryReproductionOperator
    public PropertyState getSecondaryReproductionOperatorState() {
        return this.useSecondaryReproductionOperator ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public void setSecondaryReproductionOperator(ReproductionOperator secondaryReproductionOperator) {
        this.secondaryReproductionOperator = secondaryReproductionOperator;
    }

    public boolean isUseSecondaryReproductionOperator() {
        return useSecondaryReproductionOperator;
    }

    public void setUseSecondaryReproductionOperator(boolean useSecondaryReproductionOperator) {
        this.useSecondaryReproductionOperator = useSecondaryReproductionOperator;
    }
}
