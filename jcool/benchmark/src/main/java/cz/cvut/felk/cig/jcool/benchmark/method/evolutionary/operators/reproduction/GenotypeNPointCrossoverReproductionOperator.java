package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 19:18
 * Configurable 2-parent n-point crossover reproduction operator.
 */
@Component(name = "Genotype 2 parent n-point crossover reproduction operator", description = "Makes n-point crossover of two parents")
public class GenotypeNPointCrossoverReproductionOperator extends AbstractNPointCrossoverReproductionOperator {

    @Override
    protected void reproduceInternal(Individual[] firstChildren, Individual[] secondChildren) {
        // make crossover
        for (int i = 0; i < firstChildren.length; i++){
            GenotypeRepresentation firstRepresentation = ((GenotypeRepresentation)firstChildren[i].getRepresentation());
            GenotypeRepresentation secondRepresentation = ((GenotypeRepresentation)secondChildren[i].getRepresentation());

            double parentFitness = Math.max(firstChildren[i].getFitness(), secondChildren[i].getFitness());
            firstChildren[i].setParentFitness(parentFitness);
            secondChildren[i].setParentFitness(parentFitness);
            
            boolean[] cutPointFlags = this.makeCutPointFlags(this.crossPointCount, firstRepresentation.getTotalLength());
            for (int j = 0; j < cutPointFlags.length; j++){
                if (!cutPointFlags[j]){
                    firstRepresentation.swapGenes(secondRepresentation, j, j+1);
                }
            }
        }
    }

    @Override
    public void checkConsistency(Population[] populations) throws OptimizationException{
        super.checkConsistency(populations);

        Individual[] individuals = populations[0].getIndividuals();

        if (individuals != null && this.crossPointCount >= ((GenotypeRepresentation)individuals[0].getRepresentation()).getTotalLength() ){
            String message = this.getClass().getSimpleName() + ": number of crossover points has to be smaller than genotype length. Genotype length is " + ((GenotypeRepresentation)individuals[0].getRepresentation()).getTotalLength() + ", but the crossover points are set to " + this.crossPointCount;
            throw new OptimizationException(message);
        }
    }

    public Class<? extends Representation> getAcceptableType() {
        return GenotypeRepresentation.class;
    }
}
