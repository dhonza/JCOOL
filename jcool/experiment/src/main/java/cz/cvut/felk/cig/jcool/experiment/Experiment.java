package cz.cvut.felk.cig.jcool.experiment;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.Telemetry;
import cz.cvut.felk.cig.jcool.solver.Solver;

/**
 *
 */
public final class Experiment {
    /** */
    private final Solver solver;
    /** */
    private final Function function;
    /** */
    private final OptimizationMethod<? extends Telemetry> method;

    /**
     * 
     * @param solver
     * @param function
     * @param method
     */
    public Experiment(Solver solver, Function function, OptimizationMethod<? extends Telemetry> method) {
        this.solver   = solver;
        this.function = function;
        this.method   = method;
    }

    public Function getFunction() {
        return function;
    }

    public OptimizationMethod<? extends Telemetry> getMethod() {
        return method;
    }

    public Solver getSolver() {
        return solver;
    }
}
