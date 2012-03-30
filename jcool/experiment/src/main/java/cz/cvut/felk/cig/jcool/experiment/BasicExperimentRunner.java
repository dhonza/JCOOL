/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.experiment;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.Producer;
import cz.cvut.felk.cig.jcool.core.Telemetry;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRun.ExperimentRunBuilder;
import cz.cvut.felk.cig.jcool.experiment.util.*;
import cz.cvut.felk.cig.jcool.solver.Solver;
import cz.cvut.felk.cig.jcool.solver.UserInterruptStopCondition;
import org.apache.log4j.Logger;
import org.ytoh.configurations.util.Annotations;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author ytoh
 */
public final class BasicExperimentRunner implements ExperimentRunner {

    static Logger logger = Logger.getLogger(BasicExperimentRunner.class);
    /** */
    private volatile State state;
    /** */
    private final ReentrantLock lock = new ReentrantLock();
    /** */
    private final ExecutorService es;
    /** */
    private Future<ExperimentRunBuilder> runningExperiment;
    /** */
    private Experiment currentExperiment;
    /** */
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List<TelemetryVisualization<? extends Telemetry>> visualizations;

    /**
     *
     */
    public BasicExperimentRunner() {
        this(Executors.newSingleThreadExecutor());
    }

    /**
     *
     * @param es
     */
    public BasicExperimentRunner(ExecutorService es) {
        this.es = es;
        setState(State.NOT_READY);
        newExperiment();
    }

    /**
     *
     */
    public void newExperiment() {
        if (state == State.RUNNING) {
            throw new IllegalStateException("Cannot start a new experiment, an experiment is running.");
        }

        lock.lock();
        try {
            this.currentExperiment = new Experiment(null, null, null);
            this.visualizations = new ArrayList<TelemetryVisualization<? extends Telemetry>>();
        } finally {
            lock.unlock();
        }

        updateExperimentState();
    }

    /**
     *
     */
    public void resetExperiment() {
        if (state == State.RUNNING) {
            throw new IllegalStateException("Cannot reset experiment, experiment is running.");
        }

        newExperiment();
    }

    /**
     *
     */
    public void startExperiment() {
        if (!canStartExperiment()) {
            throw new IllegalStateException("Experiment not ready. (in state " + state + ")");
        }

        final ExperimentRunBuilder builder = ExperimentRun.newInstance();

        final List<TelemetryVisualization<? extends Telemetry>> visualizationToUpdate;

        lock.lock();
        try {
            visualizationToUpdate = visualizations;
            builder.setFunction(currentExperiment.getFunction());
            builder.setMethod(currentExperiment.getMethod());
            builder.setSolver(currentExperiment.getSolver());
            this.visualizations = new ArrayList<TelemetryVisualization<? extends Telemetry>>();
        } finally {
            lock.unlock();
        }

        final Experiment experiment = builder.getExperiment();

        Aggregator<Telemetry> aggregator = Consumers.synchronizingAggregatorOf(Telemetry.class, experiment.getSolver().getClass());
        // method => aggregator
        
        Producer<? extends Telemetry> broadcast = Consumers.broadcast(experiment.getMethod());
        broadcast.addConsumer(aggregator);
        ValueTelemetryTransformer transformer = new ValueTelemetryTransformer();
        // method => value transformer
        broadcast.addConsumer(transformer);
        transformer.addConsumer(aggregator);
        // solver => aggregator
        experiment.getSolver().addConsumer(aggregator);
        // aggregator => builder
        aggregator.addConsumer(builder);

        for (TelemetryVisualization<? extends Telemetry> visualization : visualizationToUpdate) {
            initializeVisualization((TelemetryVisualization<Telemetry>) visualization, aggregator, experiment.getSolver());
            visualization.init(experiment.getFunction());
        }

        try {
            experiment.getSolver().init(experiment.getFunction(), experiment.getMethod());

            runningExperiment = es.submit(new Callable<ExperimentRunBuilder>() {

                public ExperimentRunBuilder call() throws Exception {
                    String functionName = Annotations.getName(experiment.getSolver().getClass());
                    String methodName   = Annotations.getName(experiment.getMethod().getClass());
                    String solverName   = Annotations.getName(experiment.getSolver().getClass());
                    logger.info(String.format("Starting experiment - function: %s, method: %s, solver: %s ", functionName, methodName, solverName));

                    setState(State.RUNNING);
                    try{
                        experiment.getSolver().solve();
                    } finally {
                        setState(State.FINISHED);
                        // exception will be thrown in ExperimentRunner.getExperimentResults()
                    }
                    logger.info(String.format("Experiment done. %s", experiment.getSolver().getResults().getMetConditions()));
                    return builder;
                }
            });
        } catch (Exception e){
            setState(State.FINISHED);
            logger.error("Error during solver init: " + e.getMessage());
            throw new ExperimentException("Some error occurred during startExperiment action.", e);
        }
    }

    /**
     *
     * @return
     */
    public State getExperimentState() {
        return state;
    }

    /**
     * Convenience method.
     *
     * @return
     */
    public boolean canStartExperiment() {
        return (state == State.READY || state == State.FINISHED || state == State.CANCELLED);
    }

    /**
     *
     * @return
     */
    public boolean canResetExperiment() {
        return state == State.FINISHED | state == State.CANCELLED;
    }

    /**
     *
     * @return
     */
    public boolean canCreateExperiment() {
        return state != State.RUNNING;
    }

    /**
     * 
     * @return
     */
    public boolean canStopExperiment() {
        return state == State.RUNNING;
    }

    /**
     *
     */
    public void stopExperiment() {
        if (runningExperiment != null && !runningExperiment.isDone()) {
            logger.info("Sending cancel request to solver.");
            currentExperiment.getSolver().addSystemStopCondition(new UserInterruptStopCondition());
            ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
            service.schedule(new Callable<Void>() {

                public Void call() throws Exception {
                    if(!runningExperiment.isDone()) {
                        runningExperiment.cancel(true);
                        logger.info("Experiment forcefully interrupted.");
                        setState(State.CANCELLED);
                    }
                    
                    return null;
                }
            }, 1, TimeUnit.SECONDS);
            service.shutdown();
        }
    }

    /**
     *
     * @return
     */
    public Class getTelemetryType() {
        if (currentExperiment.getMethod() != null) {
            return currentExperiment.getMethod().getValue().getClass();
        }

        return Object.class;
    }

    /**
     *
     * @param <T>
     * @param visualization
     */
    private <T extends Telemetry> void initializeVisualization(final TelemetryVisualization<T> visualization, Aggregator<T> aggregator, final Solver solver) {
        // filter out the desired value from aggregated ones
        final Filter<Telemetry, T> filter = Producers.filtering(visualization.getAcceptableType(), Telemetry.class);

        // wrapped the filtered telemetry in na Iteration wrapper
        Wrapper<T, Iteration<T>> wrapper = Producers.wrap(filter, new Transformer<T, Iteration<T>>() {

            private Solver s = solver;

            public Iteration<T> transform(T input) {
                return new Iteration<T>(input, s.getValue().getValue());
            }
        });

        // add filter as one of aggregation consumers
        aggregator.addConsumer(filter);

        /// add the visualization as a consumer of the wrapped filtered telemetry
        wrapper.addConsumer(visualization);
    }

    private void setState(State state) {
        State old = this.state;
        this.state = state;
        support.firePropertyChange("state", old, this.state);
    }

    /**
     *
     */
    private void updateExperimentState() {
        setState((currentExperiment.getFunction() != null && currentExperiment.getMethod() != null && currentExperiment.getSolver() != null) ? State.READY : State.NOT_READY);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     *
     * @param visualization
     */
    public void addVisualization(final TelemetryVisualization<? extends Telemetry> visualization) {
        if (visualization == null) {
            throw new NullPointerException("Cannot set visualization, visualization is null.");
        }

        if (state == State.RUNNING) {
            throw new IllegalStateException("Cannot set visualization, experiment is running.");
        }

        lock.lock();
        try {
            if(currentExperiment.getFunction() != null) {
                visualization.init(currentExperiment.getFunction());
            }
            visualizations.add(visualization);
        } finally {
            lock.unlock();
        }
    }

    /**
     *
     * @param function
     */
    public void setFunction(Function function) {
        if (function == null) {
            throw new NullPointerException("Cannot set function, function is null.");
        }

        if (state == State.RUNNING) {
            throw new IllegalStateException("Cannot set function, experiment is running.");
        }

        lock.lock();
        try {
            for (TelemetryVisualization<? extends Telemetry> telemetryVisualization : visualizations) {
                telemetryVisualization.init(function);
            }
            currentExperiment = new Experiment(currentExperiment.getSolver(), function, currentExperiment.getMethod());
        } finally {
            lock.unlock();
        }

        updateExperimentState();
    }

    /**
     *
     * @param method
     */
    public void setMethod(OptimizationMethod<? extends Telemetry> method) {
        if (method == null) {
            throw new NullPointerException("Cannot set method, method is null.");
        }

        if (state == State.RUNNING) {
            throw new IllegalStateException("Cannot set method, experiment is running.");
        }

        lock.lock();
        try {
            currentExperiment = new Experiment(currentExperiment.getSolver(), currentExperiment.getFunction(), method);
        } finally {
            lock.unlock();
        }

        updateExperimentState();
    }

    /**
     *
     * @param solver
     */
    public void setSolver(Solver solver) {
        if (solver == null) {
            throw new NullPointerException("Cannot set solver, solver is null.");
        }

        if (state == State.RUNNING) {
            throw new IllegalStateException("Cannot set solver, experiment is running.");
        }

        lock.lock();
        try {
            currentExperiment = new Experiment(solver, currentExperiment.getFunction(), currentExperiment.getMethod());
        } finally {
            lock.unlock();
        }

        updateExperimentState();
    }

    /**
     *
     * @return
     */
    public ExperimentRun getExperimentResults() {
        if (state == State.CANCELLED) {
            logger.info("Experiment was cancelled, no results available");
            throw new IllegalStateException("Experiment was cancelled, no results available.");
        }
        
        try {
            return runningExperiment.get().build();
        } catch (InterruptedException ex) {
            throw new ExperimentException("Experiment thread has been interrupted", ex);
        } catch (ExecutionException ex) {
            // unwrap to root cause
            String message = null;
            Throwable throwable = ex;
            while (throwable.getCause() != null){
                throwable = throwable.getCause();
            }
            message = throwable.getMessage();
            logger.info("Error during experiment run: " + message);

            throw new ExperimentException("Some error occurred during experiment run.", ex);
        }
    }
}