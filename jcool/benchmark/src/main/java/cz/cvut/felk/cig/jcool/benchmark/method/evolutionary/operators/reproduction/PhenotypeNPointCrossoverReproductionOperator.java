package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 20.2.2011
 * Time: 19:48
 * Configurable 2-parent n-point crossover reproduction operator.
 */
@Component(name = "Phenotype 2 parent n-point crossover reproduction operator", description = "Makes n-point crossover of two parents")
public class PhenotypeNPointCrossoverReproductionOperator extends AbstractNPointCrossoverReproductionOperator {

    @Override
    protected void reproduceInternal(Individual[] firstChildren, Individual[] secondChildren) {
        // make crossover
        for (int i = 0; i < firstChildren.length; i++){
            double[] firstValues = firstChildren[i].getRepresentation().getDoubleValue();
            double[] secondValues = secondChildren[i].getRepresentation().getDoubleValue();

            double parentFitness = Math.max(firstChildren[i].getFitness(), secondChildren[i].getFitness());
            firstChildren[i].setParentFitness(parentFitness);
            secondChildren[i].setParentFitness(parentFitness);
            
            double tmp;
            boolean[] cutPointFlags = this.makeCutPointFlags(this.crossPointCount, this.function.getDimension());
            for (int j = 0; j < cutPointFlags.length; j++){
                if (!cutPointFlags[j]){
                    tmp = firstValues[j];
                    firstValues[j] = secondValues[j];
                    secondValues[j] = tmp;
                }
            }
            // finally set the new values to the representation
            firstChildren[i].getRepresentation().setDoubleValue(firstValues);
            secondChildren[i].getRepresentation().setDoubleValue(secondValues);
        }
    }

    public void checkConsistency(Population[] populations) throws OptimizationException{
        super.checkConsistency(populations);
        
        if (this.crossPointCount >= this.function.getDimension()){
            String message = this.getClass().getSimpleName() + ": number of crossover points has to be smaller than function dimension. Function dimension is " + this.function.getDimension() + ", but the crossover points are set to " + this.crossPointCount;
            throw new OptimizationException(message);
        }
    }

    public Class<? extends Representation> getAcceptableType() {
        return PhenotypeRepresentation.class;
    }
}
