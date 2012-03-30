package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.methods;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.*;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.evaluators.SimpleFunctionEvaluator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.individual.SimpleIndividualFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.population.SimplePopulationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.representation.SimplePhenotypeRepresentationFactory;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.fitness.SimpleFitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.util.PopulationUtils;
import cz.cvut.felk.cig.jcool.benchmark.util.RestrictiveObjectiveFunction;
import cz.cvut.felk.cig.jcool.benchmark.util.SimpleRandomGenerator;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;
import org.ytoh.configurations.ui.DynamicDropDown;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 30.3.2011
 * Time: 23:26
 * Abstract predecessor to ease up implementation for necessary methods.
 */
public abstract class AbstractEvolutionaryMethod<T extends ValuePointListTelemetry> implements EvolutionaryOptimizationMethod<T> {

    @Property(name = "function evaluator")
    @DynamicDropDown(key = "functionEvaluators", type = FunctionEvaluator.class, label = DynamicDropDown.Label.NAME)
    protected FunctionEvaluator functionEvaluator = new SimpleFunctionEvaluator();

    @Property(name = "representation factory")
    @DynamicDropDown(key = "representationFactories", type = RepresentationFactory.class, label = DynamicDropDown.Label.NAME)
    protected RepresentationFactory representationFactory = new SimplePhenotypeRepresentationFactory();

    @Property(name = "population factory")
    @DynamicDropDown(key = "populationFactories", type = PopulationFactory.class, label = DynamicDropDown.Label.NAME)
    protected PopulationFactory populationFactory = new SimplePopulationFactory();

    @Property(name = "individual factory")
    @DynamicDropDown(key = "individualFactories", type = IndividualFactory.class, label = DynamicDropDown.Label.NAME)
    protected IndividualFactory individualFactory = new SimpleIndividualFactory();

    @Property(name = "fitness function")
    @DynamicDropDown(key = "fitnessFunctions", type = FitnessFunction.class, label = DynamicDropDown.Label.NAME)
    protected FitnessFunction fitnessFunction = new SimpleFitnessFunction();

    @Property(name = "random generator")
    @DynamicDropDown(key = "randomGenerators", type = RandomGenerator.class, label = DynamicDropDown.Label.NAME)
    protected RandomGenerator randomGenerator = new SimpleRandomGenerator();

    @Property(name = "restrict function bounds", description = "set to true enables additional restriction of function defined search space")
    protected boolean restrictFunctionBounds;

    @Property(name = "minimum function bound", description = "sets new function bound minimum")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    protected double functionMinimum = -10;

    @Property(name = "maximum function bound", description = "sets new function bound maximum")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    protected double functionMaximum = 10;

    protected ObjectiveFunction function;
    protected Population parentPopulation;
    protected ValuePoint solution;
    protected ValuePointListTelemetry telemetry;
    protected Consumer<? super T> consumer;

    /**
     * Checks presence of required operators and acceptable types.
     * @throws OptimizationException if representation type does not match reproduction operator or distance function, or some required operator or property has not been set.
     */
    public void validateConfiguration() throws OptimizationException{
        if (this.representationFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": representation factory has to be set");
        }
        if (this.populationFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": population factory has to be set");
        }
        if (this.individualFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": individual factory has to be set");
        }
        if (this.randomGenerator == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": random generator has not been set");
        }
        if (this.getPopulationSize() < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": population size cannot be less than 1, but the given value " + this.getPopulationSize() + " is");
        }
        if (this.fitnessFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": fitness function has not been set");
        }
        if (this.restrictFunctionBounds && this.functionMinimum >= this.functionMaximum){
            throw new OptimizationException(this.getClass().getSimpleName() + ": restricted lower bound has to be strictly smaller than restricted upper bound, but the lower bound " + this.functionMinimum + " >= " + this.functionMaximum);
        }
    }

    /**
     * Sets function into protected property, validates configuration and initializes basic factories.
     * @param function - function to optimize
     */
    public void init(ObjectiveFunction function){
        this.validateConfiguration();

        // additional function bounds; necessary to replace the argument due to mixed usage of this.function and function argument
        if (this.restrictFunctionBounds){
            function = new RestrictiveObjectiveFunction(function, this.functionMinimum, this.functionMaximum);
        }
        this.function = function;
        this.function.resetGenerationCount();

        this.representationFactory.setFunction(function);
        this.individualFactory.setFunction(function);
        this.individualFactory.setRandomGenerator(this.randomGenerator);
        this.individualFactory.setRepresentationFactory(this.representationFactory);
    }

    /**
     * Creates ValuePointListTelemetry of current parentPopulation.
     * If some stop condition is met, then solution is added into methodsTelemetry.
     */
    @SuppressWarnings("unchecked")
    protected void createTelemetry(){
        List<ValuePoint> list = new ArrayList<ValuePoint>();
        if (this.parentPopulation != null && this.parentPopulation.getIndividuals() != null){
            for (Individual individual : this.parentPopulation.getIndividuals()){
                list.add(ValuePoint.at(Point.at(individual.getRepresentation().getDoubleValue()), individual.getValue()));
            }
        }
        for (StopCondition stopCondition : this.getStopConditions()){
            if (stopCondition.isConditionMet()){
                list.add(this.solution);
                break;
            }
        }
        this.telemetry = new ValuePointListTelemetry(list);
    }

    public Class<? extends Representation> getRepresentationType() {
        if (this.representationFactory != null){
            return this.representationFactory.getRepresentationType();
        }
        return Representation.class;
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
     * Optionally computes parentPopulation value and/or fitness, depending on Function and FitnessFunction dynamics.
     */
    protected void computeParentsValueAndFitness(){
        if (this.function.isDynamic()){
            this.functionEvaluator.evaluate(new Population[]{this.parentPopulation}, this.function);
        }
        if (this.fitnessFunction.isDependantOnPopulationComposition() || this.fitnessFunction.isDynamic() || this.function.isDynamic() ){
            this.fitnessFunction.computeFitness(new Population[]{this.parentPopulation});
        }
    }

    public void addConsumer(Consumer<? super T> consumer) {
        this.consumer = consumer;
    }

    public void notifyConsumer(){
        if (this.consumer != null){
            consumer.notifyOf(this);
        }
    }

    @SuppressWarnings("unchecked")
    public T getValue() {
        return (T)this.telemetry;
    }


    public FunctionEvaluator getFunctionEvaluator() {
        return functionEvaluator;
    }

    public void setFunctionEvaluator(FunctionEvaluator functionEvaluator) {
        this.functionEvaluator = functionEvaluator;
    }

    public RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

    public void setRandomGenerator(RandomGenerator generator) {
        this.randomGenerator = generator;
    }

    public void setPopulationFactory(PopulationFactory populationFactory) {
        this.populationFactory = populationFactory;
    }

    public PopulationFactory getPopulationFactory() {
        return populationFactory;
    }

    public RepresentationFactory getRepresentationFactory() {
        return representationFactory;
    }

    public void setRepresentationFactory(RepresentationFactory representationFactory) {
        this.representationFactory = representationFactory;
    }

    public IndividualFactory getIndividualFactory() {
        return individualFactory;
    }

    public void setIndividualFactory(IndividualFactory individualFactory) {
        this.individualFactory = individualFactory;
    }

    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public boolean isRestrictFunctionBounds() {
        return restrictFunctionBounds;
    }

    public void setRestrictFunctionBounds(boolean restrictFunctionBounds) {
        this.restrictFunctionBounds = restrictFunctionBounds;
    }

    public double getFunctionMinimum() {
        return functionMinimum;
    }

    // enabled only if we want to restrict function bounds
    public PropertyState getFunctionMinimumState() {
        return this.restrictFunctionBounds ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public void setFunctionMinimum(double functionMinimum) {
        this.functionMinimum = functionMinimum;
    }

    public double getFunctionMaximum() {
        return functionMaximum;
    }

    // enabled only if we want to restrict function bounds
    public PropertyState getFunctionMaximumState() {
        return this.restrictFunctionBounds ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public void setFunctionMaximum(double functionMaximum) {
        this.functionMaximum = functionMaximum;
    }
}
