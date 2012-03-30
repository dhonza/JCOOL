package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.sharing;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;
import org.ytoh.configurations.ui.DynamicDropDown;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 28.3.2011
 * Time: 21:47
 * DistanceFunction used suitable for FitnessSharing Niching method.
 */
@Component(name = "Sharing DistanceFunction", description = "distance function suitable for sharing niching method")
public class SimpleSharingFunction implements SharingFunction {

    @Property(name = "alpha", description = "constant that regulates the shape of sharing distance function")
    @Range(from = -Double.MAX_VALUE, to = Double.MAX_VALUE)
    protected double alpha = 1.0;

    @Property(name = "sigma share", description = "specifies threshold of dissimilarity for distance between individuals")
    @Range(from = 0.0, to = Double.MAX_VALUE)
    protected double sigmaShare = 0.1;

    @Property(name = "distance function", description = "distance function that computes basic distance between individuals")
    @DynamicDropDown(key = "distanceFunctions", type = DistanceFunction.class, label = DynamicDropDown.Label.NAME)
    protected DistanceFunction distanceFunction;

    public double computeShareValue(Individual firstIndividual, Individual secondIndividual) {
        this.checkConsistency();

        double internalDistance = this.distanceFunction.distance(firstIndividual, secondIndividual);
        if (internalDistance < this.sigmaShare){
            return 1.0 - Math.pow((internalDistance / this.sigmaShare), this.alpha);
        } else {
            return 0.0;
        }
    }

    protected void checkConsistency(){
        if (this.distanceFunction == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": distance function has not been set!");
        }
        if (this.sigmaShare < 0.0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": sigma share cannot be smaller that zero, but the value " + this.sigmaShare + " is");
        }
    }

    /**
     * this function is representation independent - dependent is underlying FitnessFunction.
     * @return Representation Class.
     */
    public Class<? extends Representation> getAcceptableType() {
        return Representation.class;
    }

    public double getAlpha() {
        return this.alpha;
    }

    public DistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

    public double getSigmaShare() {
        return this.sigmaShare;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setDistanceFunction(DistanceFunction internalDistanceFunction) {
        if (internalDistanceFunction != this){
            this.distanceFunction = internalDistanceFunction;
        } else {
            this.distanceFunction = null;
        }
    }

    public void setSigmaShare(double sigmaShare) {
        this.sigmaShare = sigmaShare;
    }
}
