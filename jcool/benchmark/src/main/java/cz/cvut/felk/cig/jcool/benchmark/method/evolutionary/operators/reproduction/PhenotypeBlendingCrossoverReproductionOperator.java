package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Population;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.util.IndividualUtils;
import cz.cvut.felk.cig.jcool.core.*;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 19.2.2011
 * Time: 18:20
 * Blending crossover operator as presented in DeJong's Evolutionary Computation.
 */
@Component(name = "Phenotype blending crossover reproduction operator", description = "Blending crossover operator as presented in DeJong's Evolutionary Computation")
public class PhenotypeBlendingCrossoverReproductionOperator extends AbstractCrossoverReproductionOperator {

    @Property(name = "blend ratio", description = "determines how much information will child obtain from its first parent")
    @Range(from = 0.0, to = 1.0)
    protected double blendRatio = 0.5;

    public Population[] reproduce(Population[] populations) {
        this.checkConsistency(populations);

        // children initially begin as clones of their parents including Representation
        Individual[] firstParents = populations[0].getIndividuals();
        Individual[] secondParents = populations[1].getIndividuals();
        Individual[] firstChildren = IndividualUtils.makeCopy(firstParents, this.generationNumber);
        Individual[] secondChildren = new Individual[0]; // to suppress inspection warning
        if (this.createBothChildren){
            secondChildren = IndividualUtils.makeCopy(secondParents.clone(), this.generationNumber);
        }

        int retSize = Math.min(firstChildren.length, secondParents.length);
        for (int i = 0; i < retSize; i++){
            double parentFitness = Math.max(firstParents[i].getFitness(), secondParents[i].getFitness());
            firstChildren[i].setParentFitness(parentFitness);
            double[] firstChildrenValues = firstChildren[i].getRepresentation().getDoubleValue();
            double[] secondChildrenValues = new double[0]; // just to keep inspector silent
            if (this.createBothChildren){
                secondChildren[i].setParentFitness(parentFitness);
                secondChildrenValues = secondChildren[i].getRepresentation().getDoubleValue();
            }
            double[] firstParentsValues = firstParents[i].getRepresentation().getDoubleValue();
            double[] secondParentsValues = secondParents[i].getRepresentation().getDoubleValue();

            // finally cross-blend of values
            for (int j = 0; j < firstChildrenValues.length; j++){
                firstChildrenValues[j] = firstParentsValues[j] * this.blendRatio + secondParentsValues[j] * (1.0 - this.blendRatio);
                if (this.createBothChildren){
                    secondChildrenValues[j] = firstParentsValues[j] * (1.0 - this.blendRatio) + secondParentsValues[j] * this.blendRatio;
                }
            }
            // setting new value through setter ensures correct settings for all kinds of Representation (phenotype as well as genotype)
            firstChildren[i].getRepresentation().setDoubleValue(firstChildrenValues);
            if (this.createBothChildren){
                secondChildren[i].getRepresentation().setDoubleValue(secondChildrenValues);
            }
        }
        // wrapping Individuals into output array of Population
        Population firstPopulation = this.populationFactory.createPopulation(firstChildren);
        if (this.createBothChildren){
            Population secondPopulation = this.populationFactory.createPopulation(secondChildren);
            return new Population[]{firstPopulation, secondPopulation};
        } else {
            return new Population[]{firstPopulation};
        }
    }

    @Override
    public void checkConsistency(Population[] populations) throws OptimizationException{
        super.checkConsistency(populations);

        if (this.populationFactory == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": populationFactory has not been set");
        }
        if (populations == null){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] cannot be null");
        }
        if (populations.length < this.getInputArity()){
            throw new OptimizationException(this.getClass().getSimpleName() + ": input Population[] has to contain at least " + getInputArity() + " Populations");
        }
        if (this.blendRatio < 0.0 || this.blendRatio > 1.0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": blendRatio has to be in range <0.0, 1.0>, but the value " + this.blendRatio + " is not");
        }
    }

    public Class<? extends Representation> getAcceptableType() {
        return PhenotypeRepresentation.class;
    }

    public double getBlendRatio() {
        return this.blendRatio;
    }

    public void setBlendRatio(double blendRatio) {
        this.blendRatio = blendRatio;
    }
}
