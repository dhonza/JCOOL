package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.methods;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.ReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.SelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.representation.SimplePhenotypeRepresentationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.PhenotypeDifferentialEvolutionReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection.DifferentialEvolutionParentSelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.selection.DifferentialEvolutionSurvivalSelectionOperator;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;
import org.ytoh.configurations.ui.DynamicDropDown;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 2.4.2011
 * Time: 12:26
 * Implementation of differential evolution. Population has to be at least size of 4.
 * ParentSelection operator has to have outputArity 4 and individuals per population set to populationSize, due to reproductionOperator, which is special with output arity 1.
 * Survival selection operator with output arity 1 with individualsPerPopulation set to populationSize.
 */
@Component(name = "Differential evolution evolutionary method", description = "Reimplementation of ytoh's DifferentialEvolutionMethod.java")
public class DifferentialEvolutionEvolutionaryMethod extends AbstractEvolutionaryMethod{

    @Property(name = "population size", description = "population size has to be at least 4 due to special selection operator")
    @Range(from = 4, to = Integer.MAX_VALUE)
    protected int populationSize = 16;

    @Property(name = "parent selection operator")
    @DynamicDropDown(key = "selectionOperators", type = SelectionOperator.class, label = DynamicDropDown.Label.NAME)
    protected SelectionOperator parentSelectionOperator = new DifferentialEvolutionParentSelectionOperator();

    @Property(name = "primary reproduction operator")
    @DynamicDropDown(key = "reproductionOperators", type = ReproductionOperator.class, label = DynamicDropDown.Label.NAME)
    protected ReproductionOperator reproductionOperator = new PhenotypeDifferentialEvolutionReproductionOperator();

    @Property(name = "survival selection operator")
    @DynamicDropDown(key = "selectionOperators", type = SelectionOperator.class, label = DynamicDropDown.Label.NAME)
    protected SelectionOperator survivalSelectionOperator = new DifferentialEvolutionSurvivalSelectionOperator();

    protected SimpleStopCondition stopCondition;

    public DifferentialEvolutionEvolutionaryMethod() {
        // implicit representation is phenotype
        this.representationFactory = new SimplePhenotypeRepresentationFactory();

        this.stopCondition = new SimpleStopCondition();
        this.stopCondition.init(Double.POSITIVE_INFINITY, MachineAccuracy.EPSILON, MachineAccuracy.SQRT_EPSILON, 20);
        this.telemetry = new ValuePointListTelemetry();
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
        if (this.populationSize < 4){
            throw new OptimizationException(this.getClass().getSimpleName() + ": population size has to be at least 4, but " + this.populationSize + " does not meet this criterion");
        }
    }

    @Override
    public void init(ObjectiveFunction function) {
        super.init(function);

        // set bindings between operators - arity and individuals per population
        this.parentSelectionOperator.setRandomGenerator(this.randomGenerator);
        this.parentSelectionOperator.setPopulationFactory(this.populationFactory);
        this.configureParentSelectionOperator();
        this.reproductionOperator.setFunction(this.function);
        this.reproductionOperator.setPopulationFactory(this.populationFactory);
        this.reproductionOperator.setRandomGenerator(this.randomGenerator);
        this.survivalSelectionOperator.setRandomGenerator(this.randomGenerator);
        this.survivalSelectionOperator.setPopulationFactory(this.populationFactory);
        this.configureSurvivalSelectionOperator();

        // reset time dependant operators
        this.parentSelectionOperator.resetGenerationCount();
        this.reproductionOperator.resetGenerationCount();
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

    public void optimize() {
        // evaluate function value and/or fitness values
        this.computeParentsValueAndFitness();
        // select parents
        Population[] selectedParents = this.selectParentPopulations();
        // breed
        Population[] children = this.reproductionOperator.reproduce(selectedParents);

        // evaluate new individuals
        this.functionEvaluator.evaluate(children, this.function);
        // compute their fitness depending on function value and fitness function dynamics
        this.computeChildrenFitness(children);

        // choosing the best individual in parent as well as in children before some individuals will be left out
        Population[] candidatesForBestIndividual = PopulationUtils.appendPopulation(children, this.parentPopulation, true); // unification makes it more simple
        Individual bestIndividual = IndividualUtils.getBestIndividual(candidatesForBestIndividual);

        // survival selection
        Population[] competitorsForSurvival = PopulationUtils.appendPopulation(children, this.parentPopulation, true);
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
        if (this.parentSelectionOperator != this.survivalSelectionOperator){
            this.survivalSelectionOperator.nextGeneration();
        }
        this.fitnessFunction.nextGeneration();
        this.function.nextGeneration();
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
     * Configures arity and individualsPerPopulation of survivalSelectionOperator. Populations explicitly overlap.
     */
    protected void configureSurvivalSelectionOperator(){
        this.survivalSelectionOperator.setIndividualsPerPopulation(this.populationSize);
        this.survivalSelectionOperator.setInputArity(this.reproductionOperator.getOutputArity() + 1);
        this.survivalSelectionOperator.setOutputArity(1);
    }

    /**
     * Evaluates children fitness and optionally with parentPopulation as well depending on FitnessFunction dynamics.
     * @param children - populations for which to evaluate fitness.
     */
    protected void computeChildrenFitness(Population[] children){
        if (this.fitnessFunction.isDependantOnPopulationComposition()){
            // if fitness value dependant on population composition, then we must evaluate children with their parents at the same time
            Population[] forFitnessEvaluation = PopulationUtils.appendPopulation(children, this.parentPopulation, true);
            this.fitnessFunction.computeFitness(forFitnessEvaluation);
        } else { // can be dynamic but that does not bother us now
            this.fitnessFunction.computeFitness(children);
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

    public StopCondition[] getStopConditions() {
        return new StopCondition[]{this.stopCondition};  //To change body of implemented methods use File | Settings | File Templates.
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

    public void setParentSelectionOperator(SelectionOperator parentSelectionOperator) {
        this.parentSelectionOperator = parentSelectionOperator;
    }

    public ReproductionOperator getReproductionOperator() {
        return reproductionOperator;
    }

    public void setReproductionOperator(ReproductionOperator reproductionOperator) {
        this.reproductionOperator = reproductionOperator;
    }

    public SelectionOperator getSurvivalSelectionOperator() {
        return survivalSelectionOperator;
    }

    public void setSurvivalSelectionOperator(SelectionOperator survivalSelectionOperator) {
        this.survivalSelectionOperator = survivalSelectionOperator;
    }
}
