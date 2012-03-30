package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.derating;

import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 16.4.2011
 * Time: 19:19
 * "Exponential Fitness Derating Function" as described in "A sequential niche technique for multimodal function optimization" by Beasley, David and Bull
 */
@Component(name = "Exponential derating function", description = "Exponential derating function as described in \"A sequential niche technique for multimodal function optimization\" by Beasley, David and Bull")
public class ExponentialDeratingFunction implements DeratingFunction{

    @Property(name = "niche radius", description = "radius in which operates derating function")
    @Range(from = 0.0, to = Double.MAX_VALUE)
    protected double radius = 1.0;

    @Property(name = "minimum derating value", description = "value of derating multiplier that is returned if the distance is equal to zero")
    @Range(from = Double.MIN_VALUE, to = Double.MAX_VALUE)
    protected double minimumDeratingValue = Double.MIN_VALUE;

    public double getDeratedMultiplier(double distance) {
        if (distance < this.radius){
            // e^( ln(m) * (r - d) / r)
            return Math.exp( Math.log(minimumDeratingValue) * ((radius - distance) / radius ) );
        }
        return 1.0;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMinimumDeratingValue() {
        return minimumDeratingValue;
    }

    public void setMinimumDeratingValue(double minimumDeratingValue) {
        this.minimumDeratingValue = minimumDeratingValue;
    }
}
