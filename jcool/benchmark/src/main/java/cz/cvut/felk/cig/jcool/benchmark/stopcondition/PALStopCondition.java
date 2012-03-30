package cz.cvut.felk.cig.jcool.benchmark.stopcondition;

import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.utils.MachineAccuracy;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * User: drchaj1
 * Date: Dec 30, 2008
 * Time: 11:57:20 AM
 */
public class PALStopCondition implements StopCondition {
    @Property(name="Use stop condition")
    private boolean use = true;

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    private boolean isMet = false;
    private double tolerancex;
    private double tolerancefx;

    @Property(name = "number of function stops")
    @Range(from = 1, to = Integer.MAX_VALUE)
    private int numFuncStops;

    private int countFuncStops;

    private double prevfx;
    private double[] prevx;

    public PALStopCondition() {
        this(MachineAccuracy.EPSILON);
    }

    public PALStopCondition(double otolerance) {
        this(otolerance, otolerance, 4);
    }

    public PALStopCondition(double tolerancex, double tolerancefx, int numFuncStops) {
        this.tolerancex = tolerancex;
        this.tolerancefx = tolerancefx;
        this.numFuncStops = numFuncStops;
    }

    public void init(double ofx, Point ox) {
        prevfx = ofx;
        prevx = ox.toArray();
        countFuncStops = 0;
        isMet = false;
    }

    public void setValues(double ofx, Point ox) {
        boolean stop = true;
        final double[] x = ox.toArray();

        // check function argument for stop
        for (int i = 0; i < x.length; i++) {
            if (Math.abs(x[i] - prevx[i]) > tolerancex) {
                stop = false;
                break;
            }
        }

        if (!stop) {
            if (Math.abs(ofx - prevfx) <= tolerancefx) { // check function value for stop
                countFuncStops++;
            } else {
                countFuncStops = 0;
            }

            if (countFuncStops >= numFuncStops) {
                stop = true;
            }
        }

        if (!stop) {
            prevfx = ofx;
            prevx = x.clone();
        }

        isMet = stop;
    }

    public boolean isConditionMet() {
        return isMet && use;
    }

    public int getNumFuncStops() {
        return numFuncStops;
    }

    public void setNumFuncStops(int numFuncStops) {
        this.numFuncStops = numFuncStops;
        this.countFuncStops = 0;
        this.isMet = false;
    }
}
