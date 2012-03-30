package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.PhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.core.Point;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:17:21
 * Gaussian phenotype mutation operator.
 */
@Component(name = "Phenotype Gaussian mutation reproduction operator", description = "Phenotype gaussian mutation reproduction operator")
public class PhenotypeGaussianMutationReproductionOperator extends AbstractMutationReproductionOperator {

    /**
     * mean value of mutation; for internal purposes only
     */
    protected double mean = 0.0;

    /**
     * standard deviation; for internal purposes only
     */
    protected double deviation;

    @Property(name = "average mutation step size")
    @Range(from = 0.0, to = Double.MAX_VALUE)
    protected double stepSize = 0.1;

    protected static double SQRT = Math.sqrt(2.0 / Math.PI);

    public PhenotypeGaussianMutationReproductionOperator(){
        this.setStepSize(0.1);
    }

    @Override
    protected void reproduceInternal(Individual[] children, Individual[] parents) {
        // for every child
        for (int i = 0; i < children.length; i++){
            // don't forget to set parent fitness!!!
            children[i].setParentFitness(parents[i].getFitness());

            Representation representation = children[i].getRepresentation();
            double[] values = representation.getDoubleValue();
            // for every variable
            for (int j = 0; j < function.getDimension(); j++){
                // mutate variable?
                if (this.randomGenerator.nextRandom() <= this.mutationProbability){
                    values[j] = values[j] + this.randomGenerator.nextGaussian(this.mean, this.getDeviation());
                    // if not in bounds, then replace bad value with original from parent
                    if (!function.inBounds(Point.at(values))){
                        values[j] = parents[i].getRepresentation().getDoubleValue()[j];
                    }
                }
            }
            representation.setDoubleValue(values);
        }
    }

    public Class<? extends Representation> getAcceptableType() {
        return PhenotypeRepresentation.class;
    }

    public double getDeviation() {
        return this.deviation;
    }

    /**
     * Calculates deviation from given step size.
     * @param stepSize - step size to convert to deviation.
     * @return deviation computed from given step size.
     */
    public double getDeviation(double stepSize) {
        return stepSize / SQRT;
    }

    public double getStepSize() {
        return this.stepSize;
    }

    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
        this.deviation = this.getDeviation(stepSize);
    }
}
