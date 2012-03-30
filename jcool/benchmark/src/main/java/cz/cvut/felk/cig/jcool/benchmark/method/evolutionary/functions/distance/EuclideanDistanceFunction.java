package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.distance;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 16:07:06
 * Euclidean distance function of two Individuals with any kind of Representation.  
 */
@Component(name = "Euclidean distance function", description = "Function measuring distance of Individuals depending on Euclidean distance of their function values")
public class EuclideanDistanceFunction implements DistanceFunction {

    public double distance(Individual firstIndividual, Individual secondIndividual) {
        double[] first = firstIndividual.getRepresentation().getDoubleValue();
        double[] second = secondIndividual.getRepresentation().getDoubleValue();
        double distance = 0.0;
        double ba;
        for (int i = 0; i < first.length; i++){
            ba = second[i] - first[i];
            distance += ba * ba;
        }
        return Math.sqrt(distance);
    }

    public Class<? extends Representation> getAcceptableType() {
        return Representation.class;
    }
}
