package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 18:08
 * Uniform crossover operator operating upon GenotypeRepresentation.
 */
@Component(name = "Genotype uniform crossover reproduction operator", description = "Makes uniform crossover of two given parents")
public class GenotypeUniformCrossoverReproductionOperator extends AbstractUniformCrossoverReproductionOperator{

    @Override
    protected void reproduceInternal(Individual[] firstChildren, Individual[] secondChildren, int retSize) {
        for (int i = 0; i < retSize; i++){
            GenotypeRepresentation firstRepresentation = ((GenotypeRepresentation)firstChildren[i].getRepresentation());
            GenotypeRepresentation secondRepresentation = ((GenotypeRepresentation)secondChildren[i].getRepresentation());

            double parentFitness = Math.max(firstChildren[i].getFitness(), secondChildren[i].getFitness());
            firstChildren[i].setParentFitness(parentFitness);
            secondChildren[i].setParentFitness(parentFitness);
            
            // finally crossover of values
            int totalLength = firstRepresentation.getTotalLength();
            for (int j = 0; j < totalLength; j++){
                // if probability is high, then swap corresponding values
                if (this.crossoverProbability > 0.0 && this.randomGenerator.nextRandom() <= this.crossoverProbability){
                    firstRepresentation.swapGenes(secondRepresentation, j, j+1);
                }
            }
        }
    }

    public Class<? extends Representation> getAcceptableType() {
        return GenotypeRepresentation.class;
    }
}
