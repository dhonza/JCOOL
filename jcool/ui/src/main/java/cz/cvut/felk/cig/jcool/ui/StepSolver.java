/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui;

import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.Producer;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.solver.OptimizationResults;
import cz.cvut.felk.cig.jcool.solver.Solver;
import cz.cvut.felk.cig.jcool.solver.Synchronization;
import java.util.HashSet;
import org.apache.log4j.Logger;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 *
 * @author ytoh
 */
@Component(name = "Stepping solver", description = "Solver that waits a configurable " +
"period of time between optimization steps.")
public class StepSolver implements Solver, Consumer<Synchronization> {
    static Logger logger = Logger.getLogger(StepSolver.class);

    @Property(name = "Wait time [ms]", description = "Time in miliseconds to wait between optimization steps")
    @Range(from = 0, to = 60000)
    private double waitTime;

    public double getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }

    private Solver delegate;
    private Consumer<? super Synchronization> consumer;

    public StepSolver(Solver delegate) {
        this.delegate = delegate;
    }

    public void init(Function function, OptimizationMethod method) throws Exception{
        delegate.init(function, method);
        delegate.addConsumer(this);
    }

    public void addSystemStopCondition(StopCondition condition) {
        delegate.addSystemStopCondition(condition);
    }

    public void solve() throws Exception {
        // makes the delegate stop after every iteration
        delegate.addSystemStopCondition(new StopCondition() {
            private int i = 1;
            public boolean isConditionMet() {
                return (i++ % 2) == 0;
            }
        });
        
        // if there is only the above stop condition present do another
        // optimization step in waitTime miliseconds
        while (new HashSet<StopCondition>(delegate.getResults().getMetConditions()).size() <= 1) {
            delegate.solve();
            try {
                logger.info("sleeping for: " + (long) waitTime + "ms");
                Thread.sleep((long) waitTime);
            } catch (InterruptedException ex) {
                throw new OptimizationException("Interrupted", ex);
            }
        }
    }

    public OptimizationResults getResults() {
        return delegate.getResults();
    }

    public void addConsumer(Consumer<? super Synchronization> consumer) {
        this.consumer = consumer;
    }

    public Synchronization getValue() {
        return delegate.getValue();
    }

    public void notifyOf(Producer<? extends Synchronization> producer) {
        logger.info("got a notification from: " + producer.getClass());
        if(consumer != null) {
            logger.info("notifying consumer: " + consumer.getClass());
            consumer.notifyOf(this);
        }
    }
}
