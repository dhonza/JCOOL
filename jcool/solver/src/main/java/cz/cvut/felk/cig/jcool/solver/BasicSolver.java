/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.solver;

import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.utils.CentralDifferenceGradient;
import cz.cvut.felk.cig.jcool.utils.CentralDifferenceHessian;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple solver encapsulating the optimization method calculation and checking
 * method stop condition together with a max iteration stop condition.
 *
 * @author ytoh
 */
@Component(name="Basic solver", description="Simple solver encapsulating the optimization method calculation and checking method stop condition together with a max iteration stop condition.")
public class BasicSolver implements Solver {
    static final Logger logger = Logger.getLogger(BasicSolver.class);

    private Function                                function;
    private StopCondition[]                         methodConditions;
    private StopCondition[]                         systemConditions;
    private IterationStopCondition                  iterations;
    private Synchronization                         synchronization;
    private Consumer<? super Synchronization>       synchronizationConsumer;
    private OptimizationMethod<? extends Telemetry> method;
    private BaseObjectiveFunction                   baseObjectiveFunction;
    // convenience shortcut
    private List<StopCondition>                     metConditions;

    @Property(name="Maximum number of interations", description="How many optimization steps are allowed before the optimization process is stopped.")
    @Range(from=1, to=Integer.MAX_VALUE)
    private int maxIterations = Integer.MAX_VALUE;

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Property(name="Use delay")
    private boolean useDelay = false;

    public boolean isUseDelay() {
        return useDelay;
    }

    public void setUseDelay(boolean useDelay) {
        this.useDelay = useDelay;
    }

    @Property(name="Delay between steps",description="How many miniseconds should this solver wait before doing another optimization step")
    @Range(from=0,to=Integer.MAX_VALUE)
    private int milisDelay;

    public int getMilisDelay() {
        return milisDelay;
    }

    public void setMilisDelay(int milisDelay) {
        this.milisDelay = milisDelay;
    }

    public PropertyState getMilisDelayState() {
        return useDelay ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    /**
     * Constructs an instance of <code>BasicSolver</code> with a special system
     * stop condition making sure that the optimization calculation doesn't
     * exceed a certain count.
     *
     * @param maxIterations maximum number of main cycle iterations
     */
    BasicSolver(int maxIterations) {
        assert maxIterations > 0;

        this.maxIterations = maxIterations;
    }

    public void init(Function function, OptimizationMethod method) throws Exception {
        this.function = function;
        this.method = method;
        
        baseObjectiveFunction = new BaseObjectiveFunction(function);
        if(!baseObjectiveFunction.hasAnalyticalGradient()) {
            baseObjectiveFunction.setNumericalGradient(new CentralDifferenceGradient());
        }
        
        if(!baseObjectiveFunction.hasAnalyticalHessian()) {
            baseObjectiveFunction.setNumericalHessian(new CentralDifferenceHessian());
        }
        
        synchronization = new Synchronization();

        method.init(baseObjectiveFunction);

        // get method specific stop conditions
        methodConditions = (StopCondition[]) ArrayUtils.clone(method.getStopConditions());
        metConditions = new ArrayList<StopCondition>();

        iterations = new IterationStopCondition(maxIterations);
        systemConditions = new StopCondition[] {iterations};
    }

    public void addSystemStopCondition(StopCondition condition) {
        systemConditions = (StopCondition[]) ArrayUtils.add(systemConditions, condition);
    }

    public void solve() throws Exception{
        int iteration = 0;
        logger.debug("main cycle start");

        while (checkStopConditions()) {
            logger.debug("main cycle");

            if(useDelay) {
                try {
                    Thread.sleep(milisDelay);
                } catch (InterruptedException ex) {
                    throw new OptimizationException("Stopping optimization", ex);
                }
            }

            logger.info(iteration + " " +baseObjectiveFunction.getStatistics());

            synchronization = new Synchronization(++iteration);
            if(synchronizationConsumer != null) {
                synchronizationConsumer.notifyOf(this);
            }
            iterations.nextIteration();

            // main optimization cycle
            method.optimize();            
        }
    }

    /**
     * Convenience method for checking stop condition satisfaction.
     *
     * @return true if any of the custom/system stop conditions have been met,
     * else if no stop conditions have been met
     */
    private boolean checkStopConditions() {
        logger.debug("checking stop conditions");

        for (int i = 0; i < methodConditions.length; i++) {
            if (methodConditions[i].isConditionMet()) {
                if(logger.isDebugEnabled()) {
                    logger.debug("condition met: " + methodConditions[i]);
                }

                metConditions.add(methodConditions[i]);
            }
        }
        for (int i = 0; i < systemConditions.length; i++) {
            if (systemConditions[i].isConditionMet()) {
                if(logger.isDebugEnabled()) {
                    logger.debug("condition met: " + systemConditions[i]);
                }

                metConditions.add(systemConditions[i]);
            }
        }

        return metConditions.isEmpty();
    }

    public OptimizationResults getResults() {
        return new OptimizationResults(method.getValue(), baseObjectiveFunction.getStatistics(), synchronization.getValue(), metConditions);
    }

    public void addConsumer(Consumer<? super Synchronization> consumer) {
        this.synchronizationConsumer = consumer;
    }

    public Synchronization getValue() {
        return synchronization;
    }
}
