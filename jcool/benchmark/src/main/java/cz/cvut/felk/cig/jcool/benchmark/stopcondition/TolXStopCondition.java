package cz.cvut.felk.cig.jcool.benchmark.stopcondition;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.ytoh.configurations.annotations.Property;

/**
 * @author sulcanto
 */
public class TolXStopCondition implements CMAESStopCondition {

    @Property(name = "Use")
    private boolean use = true;

    @Property(name = "TolX")
    double tolX;

    RealVector pc;
    double sigma;
    RealVector diagC;
    int N;

    public void init(int N) {
        this.N = N;
        // initial values, stop conditions not met initially
        this.pc = this.diagC = MatrixUtils.createRealVector(new double[N]).mapAdd(1d);
        this.sigma = 1d;


    }


    public boolean isConditionMet() {
        if (use == false)
            return false;
        double[] pcAndSigma = pc.mapMultiply(sigma).getData();
        for (int i = 0; i < N; i++)
            if (pcAndSigma[i] > tolX)
                return false;

        double[] stdDevDistribution = diagC.mapMultiply(sigma).getData();
        for (int i = 0; i < N; i++)
            if (stdDevDistribution[i] > tolX)
                return false;
        return true;

    }

    public void setValues(RealVector pc, RealMatrix C, double sigma) {
        for(int i = 0; i < N; i++)
            this.diagC.setEntry(i,C.getEntry(i,i));
        this.pc = pc;
        this.sigma = sigma;
    }

    @Override
    public String toString() {
        return "TolX - normal distribution (eigenvalues) or sigma*pc is smaller than TolX in all axes";
    }

    /////

    public double getTolX() {
        return tolX;
    }

    public void setTolX(double tolX) {
        this.tolX = tolX;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}
