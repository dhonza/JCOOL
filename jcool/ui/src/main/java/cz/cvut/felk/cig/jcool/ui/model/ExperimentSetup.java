/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.model;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.core.Telemetry;
import cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization;
import cz.cvut.felk.cig.jcool.solver.Solver;
import cz.cvut.felk.cig.jcool.ui.view.CurrentValueVisualization;
import cz.cvut.felk.cig.jcool.ui.view.MultiPointTracker;
import cz.cvut.felk.cig.jcool.ui.view.MultiPointTrackerColored;
import cz.cvut.felk.cig.jcool.ui.view.NullVisualization;
import cz.cvut.felk.cig.jcool.ui.view.SinglePointTracker;
import cz.cvut.felk.cig.jcool.ui.view.VisualizationControls;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.context.MutableContext;
import org.ytoh.configurations.ui.DynamicDropDown;
import org.ytoh.configurations.ui.DynamicDropDown.Label;

/**
 *
 * @author ytoh
 */
@Component(name = "Experiment")
public class ExperimentSetup {
    /** */
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    @Property(name = "Function")
    @DynamicDropDown(type = Function.class, key = "functions", label = Label.NAME)
    private Function function;

    @Property(name = "Optimization method")
    @DynamicDropDown(type = OptimizationMethod.class, key = "methods", label = Label.NAME)
    private OptimizationMethod optimizationMethod;

    @Property(name = "Solver")
    @DynamicDropDown(type = Solver.class, key = "solvers", label = Label.NAME)
    private Solver solver;

    private List<StopCondition> stopConditions;

    private List<? extends TelemetryVisualization<? extends Telemetry>> visualizations;

    private static final List<? extends TelemetryVisualization<? extends Telemetry>> VISUALIZATIONS;
    private static NullVisualization nullVisualization;
    private static CurrentValueVisualization currentValueVisualization;
    static {
        VISUALIZATIONS = Collections.unmodifiableList(Arrays.asList(new CurrentValueVisualization(), new SinglePointTracker(), new MultiPointTracker(), new MultiPointTrackerColored()));
        nullVisualization = new NullVisualization();
        currentValueVisualization = new CurrentValueVisualization();
    }

    private MutableContext context;

    private VisualizationControls visualizationControls;

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        Function old = this.function;
        this.function = function;
        support.firePropertyChange("function", old, function);
    }

    public OptimizationMethod getOptimizationMethod() {
        return optimizationMethod;
    }

    @SuppressWarnings("unchecked")
    public void setOptimizationMethod(final OptimizationMethod optimizationMethod) {
        OptimizationMethod old = this.optimizationMethod;
        this.optimizationMethod = optimizationMethod;
        support.firePropertyChange("method", old, optimizationMethod);
        if(optimizationMethod != null) {
            setStopConditions(Arrays.asList(optimizationMethod.getStopConditions()));

            List<TelemetryVisualization<? extends Telemetry>> select = (List<TelemetryVisualization<? extends Telemetry>>) CollectionUtils.select(VISUALIZATIONS, new Predicate() {

                public boolean evaluate(Object o) {
                    return o instanceof TelemetryVisualization && ((TelemetryVisualization) o).getAcceptableType().isAssignableFrom(optimizationMethod.getValue().getClass());
                }
            });
            
            select.add(nullVisualization);
            select.add(currentValueVisualization);
            setVisualizations(select);
            visualizationControls.initComponents();
        }
    }

    public Solver getSolver() {
        return solver;
    }

    public void setSolver(Solver solver) {
        Solver old = this.solver;
        this.solver = solver;
        support.firePropertyChange("solver", old, solver);
    }

    public List<StopCondition> getStopConditions() {
        return stopConditions;
    }

    private void setStopConditions(List<StopCondition> stopConditions) {
        List<StopCondition> old = this.stopConditions;
        this.stopConditions = stopConditions;
        support.firePropertyChange("stopconditions", old, this.stopConditions);
    }

    public List<? extends TelemetryVisualization<? extends Telemetry>> getVisualizations() {
        return visualizations;
    }

    private void setVisualizations(List<? extends TelemetryVisualization<? extends Telemetry>> visualizations) {
        List<? extends TelemetryVisualization<? extends Telemetry>> old = this.visualizations;
        this.visualizations = visualizations;
        context.register(TelemetryVisualization.class, this.visualizations, "visualizations");
        support.firePropertyChange("visualizations", old, this.visualizations);
    }

    public void setContext(MutableContext context) {
        this.context = context;
    }

    public void setVisualizationControls(VisualizationControls visualizationControls) {
        this.visualizationControls = visualizationControls;
    }

    //////////////////////////////
    // Property change support
    //////////////////////////////
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }
}
