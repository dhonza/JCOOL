package cz.cvut.felk.cig.jcool.benchmark.stopcondition;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.ytoh.configurations.annotations.Property;

/**
 * @author sulcanto
 */
public class NoEffectAxisStopCondition implements CMAESStopCondition {

    @Property(name = "Use")
    private boolean use = true;

    @Property(name = "Standard deviation ratio")
    private double standardDeviationRatio = 0.1;

    private RealVector D;
    private RealMatrix B;
    private RealVector xmean;
    private int currentGeneration;
    private int N;

    public void init(int N) {
        this.N = N;
        // initial values, stop conditions not met initially
        this.xmean = this.D = MatrixUtils.createRealVector(new double[N]).mapAdd(1d);
        this.B = MatrixUtils.createRealIdentityMatrix(N);
    }

    public boolean isConditionMet() {
        if (use == false)
            return false;
        int eigenIndex = currentGeneration % N;
        RealVector xmeanAfterAddingStdDev = xmean.add(
                B.getColumnVector(eigenIndex).mapMultiply(standardDeviationRatio * Math.sqrt(D.getEntry(eigenIndex))));
        if (xmean.equals(xmeanAfterAddingStdDev)) {
            return true;
        }
        return false;
    }

    public void setValues(RealVector D, RealMatrix B, RealVector xmean, int currentGeneration) {
        this.D = D;
        this.B = B;
        this.xmean = xmean;
        this.currentGeneration = currentGeneration;
    }

    @Override
    public String toString() {
        return "No effect to axis - adding 0.1 stddev to mean is same as mean";
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
