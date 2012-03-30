/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.experiment;

import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.Producer;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.core.Telemetry;
import cz.cvut.felk.cig.jcool.solver.OptimizationResults;
import cz.cvut.felk.cig.jcool.solver.Solver;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.ytoh.configurations.util.ComponentInfo;

/**
 *
 * @author ytoh
 */
public final class ExperimentRun {
    //

    private final ComponentInfo function;
    //
    private final ComponentInfo solver;
    //
    private final ComponentInfo method;
    //
    private final List<ComponentInfo> stopConditions;
    //
    private final OptimizationResults results;
    //
    private final List<List<? extends Telemetry>> progress;

    /**
     *
     * @param function
     * @param solver
     * @param method
     * @param stopConditions
     * @param results
     * @param progress
     */
    public ExperimentRun(ComponentInfo function, ComponentInfo solver, ComponentInfo method, List<ComponentInfo> stopConditions, OptimizationResults results, List<List<? extends Telemetry>> progress) {
        this.function = function;
        this.solver = solver;
        this.method = method;
        this.stopConditions = stopConditions;
        this.results = results;
        this.progress = progress;
    }

    /**
     *
     * @return
     */
    public static ExperimentRunBuilder newInstance() {
        return new ExperimentRunBuilder();
    }

    /**
     *
     * @return
     */
    public ComponentInfo getFunction() {
        return function;
    }

    /**
     *
     * @return
     */
    public ComponentInfo getMethod() {
        return method;
    }

    /**
     * 
     * @return
     */
    public List<ComponentInfo> getStopConditions() {
        return stopConditions;
    }

    /**
     *
     * @return
     */
    public List<List<? extends Telemetry>> getProgress() {
        return progress;
    }

    /**
     *
     * @return
     */
    public OptimizationResults getResults() {
        return results;
    }

    /**
     * 
     * @return
     */
    public ComponentInfo getSolver() {
        return solver;
    }

    /**
     *
     */
    public static final class ExperimentRunBuilder implements Consumer<List<? extends Telemetry>> {
        //

        static Logger logger = Logger.getLogger(ExperimentRunBuilder.class);
        /** */
        private Solver solver;
        /** */
        private Function function;
        /** */
        private OptimizationMethod<? extends Telemetry> method;
        /** */
        private final List<List<? extends Telemetry>> progress;

        /**
         *
         */
        ExperimentRunBuilder() {
            this.progress = new ArrayList<List<? extends Telemetry>>();
        }

        public void setFunction(Function function) {
            this.function = function;
        }

        public void setMethod(OptimizationMethod<? extends Telemetry> method) {
            this.method = method;
        }

        public void setSolver(Solver solver) {
            this.solver = solver;
        }

        public void notifyOf(Producer<? extends List<? extends Telemetry>> producer) {
            progress.add(producer.getValue());
        }

        public List<List<? extends Telemetry>> getProgress() {
            return progress;
        }

        private ComponentInfo getComponentInfo(Object o) {
            try {
                return ComponentInfo.getInfo(o);
            } catch (InvocationTargetException ex) {
                logger.warn("Cannot extract component info from object: " + String.valueOf(o), ex);
            } catch (NoSuchMethodException ex) {
                logger.warn("Cannot extract component info from object: " + String.valueOf(o), ex);
            } catch (IllegalAccessException ex) {
                logger.warn("Cannot extract component info from object: " + String.valueOf(o), ex);
            }

            return null;
        }

        /**
         *
         * @return
         */
        public ExperimentRun build() {
            final ComponentInfo functionComponent = getComponentInfo(function);
            final ComponentInfo methodComponent = getComponentInfo(method);
            final ComponentInfo solverComponent = getComponentInfo(solver);
            final List<ComponentInfo> stopConditonComponents = new ArrayList<ComponentInfo>();

            for (StopCondition stopCondition : method.getStopConditions()) {
                stopConditonComponents.add(getComponentInfo(stopCondition));
            }

            return new ExperimentRun(functionComponent, solverComponent, methodComponent, stopConditonComponents, solver.getResults(), progress);
        }

        /**
         * 
         * @return
         */
        public Experiment getExperiment() {
            return new Experiment(solver, function, method);
        }
    }
}
