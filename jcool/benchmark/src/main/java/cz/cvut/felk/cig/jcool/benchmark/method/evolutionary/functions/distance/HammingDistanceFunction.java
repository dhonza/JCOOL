package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.functions.distance;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:18:15
 * Computes Hamming distance of two Individuals with GenotypeRepresentation.
 */
@Component(name = "Hamming distance function", description = "Function measuring distance of Individuals with Genotype representation depending on Hamming distance")
public class HammingDistanceFunction implements DistanceFunction{
    
    public double distance(Individual firstIndividual, Individual secondIndividual) {
        GenotypeRepresentation first = (GenotypeRepresentation)firstIndividual.getRepresentation();
        GenotypeRepresentation second = (GenotypeRepresentation)secondIndividual.getRepresentation();
        int distance = 0;
        for (int i = 0; i < first.getTotalLength(); i++){
            if (first.getGeneAt(i) ^ second.getGeneAt(i))
                distance++;
        }
        return distance;
    }

    public Class<? extends Representation> getAcceptableType() {
        return GenotypeRepresentation.class;
    }
}
