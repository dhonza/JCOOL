package cz.cvut.felk.cig.jcool.benchmark.stopcondition;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;

import java.util.Arrays;

/**
 * @author sulcanto
 */
@Component(
        name = "Eigenvalue stop condition",
        description = "Condition number of covariance matrix"
)
public class ConditionCovStopCondition implements CMAESStopCondition {
    @Property(name = "Use")
    private boolean use = true;

    @Property(name = "Condition number of covariance matrix")
    private double maxMultiplyOfMinEigenvalue = 1e7;

    private double[] eigenvalues;


    /**
     * Initialize the stop condition.
     */
    public void init(double maxMultiplyOfMinEigenvalue) {
        this.maxMultiplyOfMinEigenvalue = maxMultiplyOfMinEigenvalue;
    }

    public boolean isConditionMet() {
        if (use == false)
            return false;
        if (eigenvalues != null) {
            Arrays.sort(eigenvalues);
            if (eigenvalues[0] > maxMultiplyOfMinEigenvalue * eigenvalues[eigenvalues.length - 1])
                return true;
        }
        return false;
    }

    public double[] getEigenvalues() {
        return eigenvalues;
    }

    public void setEigenvalues(double[] eigenvalues) {
        this.eigenvalues = eigenvalues;
    }

    public double getMaxMultiplyOfMinEigenvalue() {
        return maxMultiplyOfMinEigenvalue;
    }

    public void setMaxMultiplyOfMinEigenvalue(double maxMultiplyOfMinEigenvalue) {
        this.maxMultiplyOfMinEigenvalue = maxMultiplyOfMinEigenvalue;
    }

    @Override
    public String toString() {
        return "Condition number (maxEigenValue/minEigenValue) of Covariance matrix is greater than " + maxMultiplyOfMinEigenvalue;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}
