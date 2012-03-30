package cz.cvut.felk.cig.jcool.benchmark.stopcondition;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealVector;
import org.ytoh.configurations.annotations.Property;

/**
 * @author sulcanto
 */
public class NoEffectCoordStopCondition implements CMAESStopCondition {

    @Property(name = "Use")
    private boolean use = true;

    @Property(name = "Standard deviation ratio")
    private double standardDeviationRatio = 0.2;


    RealVector D;
    RealVector xmean;
    double sigma;

    public void init(int N) {
        // initial values, stop conditions not met initially
        this.D = this.xmean = MatrixUtils.createRealVector(new double[N]).mapAdd(1d);
        sigma = Double.MAX_VALUE;
    }

    public boolean isConditionMet() {
        if (use == false)
            return false;
        if (D.mapSqrt().mapMultiply(sigma * 0.2).add(xmean).equals(xmean))
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "No effect to coordinates - adding 0.2 *eigenvalues* + mean = mean";

    }

    public void setValues(RealVector D, RealVector xmean, double sigma) {
        this.D = D;
        this.xmean = xmean;
        this.sigma = sigma;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public double getStandardDeviationRatio() {
        return standardDeviationRatio;
    }

    public void setStandardDeviationRatio(double standardDeviationRatio) {
        this.standardDeviationRatio = standardDeviationRatio;
    }
}
