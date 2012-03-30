package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.distance;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.*;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 16:07:20
 * Computes Manhattan distance of two Individuals with GenotypeRepresentation.
 */
@Component(name = "Manhattan distance function", description = "Function measuring distance of Individuals depending on Manhattan distance of of their function values")
public class ManhattanDistanceFunction implements DistanceFunction {

    public double distance(Individual firstIndividual, Individual secondIndividual) {
        double[] first = firstIndividual.getRepresentation().getDoubleValue();
        double[] second = secondIndividual.getRepresentation().getDoubleValue();
        double distance = 0.0;
        for (int i = 0; i < first.length; i++){
            distance += Math.abs(second[i] - first[i]);
        }
        return distance;
    }

    public Class<? extends Representation> getAcceptableType() {
        return Representation.class;
    }
}
