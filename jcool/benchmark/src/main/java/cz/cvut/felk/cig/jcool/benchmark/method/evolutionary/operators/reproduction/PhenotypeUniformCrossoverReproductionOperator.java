package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 19.2.2011
 * Time: 15:14
 * Uniform crossover operator operating upon PhenotypeRepresentation.
 */
@Component(name = "Phenotype uniform crossover reproduction operator", description = "Makes uniform crossover of two given parents")
public class PhenotypeUniformCrossoverReproductionOperator extends AbstractUniformCrossoverReproductionOperator {

    protected void reproduceInternal(Individual[] firstChildren, Individual[] secondChildren, int retSize){
        for (int i = 0; i < retSize; i++){
            double[] firstValues = firstChildren[i].getRepresentation().getDoubleValue();
            double[] secondValues = secondChildren[i].getRepresentation().getDoubleValue();

            double parentFitness = Math.max(firstChildren[i].getFitness(), secondChildren[i].getFitness());
            firstChildren[i].setParentFitness(parentFitness);
            secondChildren[i].setParentFitness(parentFitness);
            
            // finally crossover of values
            double tmp;
            for (int j = 0; j < firstValues.length; j++){
                // if probability is high, then swap corresponding values
                if (this.crossoverProbability > 0.0 && this.randomGenerator.nextRandom() <= this.crossoverProbability){
                    tmp = firstValues[j];
                    firstValues[j] = secondValues[j];
                    secondValues[j] = tmp;
                }
            }
            // setting new value through setter ensures correct settings for all kinds of Representation (phenotype as well as genotype)
            firstChildren[i].getRepresentation().setDoubleValue(firstValues);
            secondChildren[i].getRepresentation().setDoubleValue(secondValues);
        }
    }

    public Class<? extends Representation> getAcceptableType() {
        return PhenotypeRepresentation.class;
    }
}
