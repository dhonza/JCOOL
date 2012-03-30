package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.methods;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.EvolutionaryOptimizationMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.fitness.SimpleSuppressingFitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.fitness.SuppressingFitnessFunction;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.IterationStopCondition;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.ui.DynamicDropDown;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 15.4.2011
 * Time: 11:07
 * Implementation of sequential niching optimization method. Iterates some Evolutionary method and alters fitness landscape after every run of underlying evolutionary method.
 */
@Component(name = "Sequential niching evolutionary method", description = "Iterates chosen Evolutionary method and alters fitness landscape to suppress areas of previously found solutions")
public class SequentialNichingEvolutionaryMethod<T extends ValuePointListTelemetry> implements OptimizationMethod<T> {

    @Property(name = "evolutionary method", description = "evolutionary method to iterate")
    @DynamicDropDown(key = "evolutionaryMethods", type = EvolutionaryOptimizationMethod.class, label = DynamicDropDown.Label.NAME)
    protected EvolutionaryOptimizationMethod<T> method;

    @Property(name = "suppressing fitness function", description = "Fitness function that will replace chosen fitness function in chosen Evolutionary method")
    @DynamicDropDown(key = "suppressingFitnessFunctions", type = SuppressingFitnessFunction.class, label = DynamicDropDown.Label.NAME)
    protected SuppressingFitnessFunction suppressingFitnessFunction = new SimpleSuppressingFitnessFunction();

    protected ObjectiveFunction function;
    protected IterationStopCondition stopCondition;
    protected Consumer<? super T> consumer;
    protected ValuePointListTelemetry methodsTelemetry;
    protected ValuePointListTelemetry telemetry;
    protected List<ValuePoint> solutions;

    public SequentialNichingEvolutionaryMethod() {
        this.stopCondition = new IterationStopCondition();
        this.stopCondition.init(10);
        this.telemetry = new ValuePointListTelemetry();
        this.solutions = new ArrayList<ValuePoint>();
    }

    public void validateConfiguration() throws OptimizationException{
        if (this.method == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": evolutionary method for iteration has to be set");
        }
        if (this.suppressingFitnessFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": suppressing fitness function has to be set");
        }
        if (this.stopCondition.getNumIterations() < 1){
            throw new OptimizationException(this.getClass().getSimpleName() + ": number of iterations has to be positive, but given value is " + this.stopCondition.getNumIterations());
        }
    }

    public void init(ObjectiveFunction function) {
        this.validateConfiguration();

        this.function = function;

        this.solutions.clear();
        this.suppressingFitnessFunction.resetSuppressingPoints();
        this.method.setFitnessFunction(this.suppressingFitnessFunction);
        this.method.init(function);

        this.stopCondition.init();
        System.out.println("SequentialNichingEvolutionaryMethod start");
    }

    public void optimize() {
        this.method.optimize();
        this.methodsTelemetry = this.method.getValue(); // returns current method's telemetry
        // if stopCondition is met, then get results, alter specialFitnessFunction and update iterations stop condition as well
        if (this.isSomeStopConditionMet()){
            this.stopCondition.nextIteration(); // sets stop condition to isConditionMet state so we can resolve it in getValue method
            this.saveSolution();
            // don't forget to restart the method!
            this.method.init(this.function);
        }
        
        if (this.consumer != null){
            consumer.notifyOf(this);
        }
    }

    /**
     * Stores value point with lowest value from the last methods telemetry.
     * Used to save solution from telemetry returned as result from underlying evolutionary method.
     */
    protected void saveSolution(){
        ValuePoint best = null;
        for (ValuePoint valuePoint : this.methodsTelemetry.getValue()) {
            if (best == null){
                best = valuePoint;
            } else if (best.getValue() > valuePoint.getValue()){
                best = valuePoint;
            }
        }
        if (best != null){
            this.suppressingFitnessFunction.addSuppressingPoint(best.getPoint());
            this.solutions.add(best);
            System.out.println("Found solution: " + best);
            this.telemetry = new ValuePointListTelemetry(this.solutions);
        }
    }

    /**
     * Returns true if some of active internal method's stop condition is met.
     * @return true if some of active internal method's stop condition is met.
     */
    protected boolean isSomeStopConditionMet(){
        for (StopCondition stopCondition : this.method.getStopConditions()){
            if (stopCondition.isConditionMet()){
                return true;
            }
        }
        return false;
    }

    public StopCondition[] getStopConditions() {
        return new StopCondition[]{this.stopCondition};
    }

    public void addConsumer(Consumer<? super T> consumer) {
        this.consumer = consumer;
    }

    /**
     * Returns telemetry either from inner evolutionary method or if stop condition is met, then telemetry with all best solutions is returned.
     * @return telemetry from inner method or telemetry with all solutions if the stopCondition is met.
     */
    @SuppressWarnings("unchecked")
    public T getValue() {
        if (this.method != null && !this.stopCondition.isConditionMet() ){
            return this.method.getValue();
        } else {
            return (T)this.telemetry;
        }
    }

    public EvolutionaryOptimizationMethod<T> getMethod() {
        return method;
    }

    public void setMethod(EvolutionaryOptimizationMethod<T> method) {
        this.method = method;
    }

    public SuppressingFitnessFunction getSuppressingFitnessFunction() {
        return suppressingFitnessFunction;
    }

    public void setSuppressingFitnessFunction(SuppressingFitnessFunction suppressingFitnessFunction) {
        this.suppressingFitnessFunction = suppressingFitnessFunction;
    }
}
