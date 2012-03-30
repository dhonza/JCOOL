package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Individual;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 27.3.2011
 * Time: 14:20
 * BitFlip mutation reproduction operator.
 */
@Component(name = "Genotype mutation reproduction operator", description = "Performs bit-flip mutation upon given genotype")
public class GenotypeMutationReproductionOperator extends AbstractMutationReproductionOperator {

    public enum ProbabilityType{
        ExactProbability("exact probability"), FractionalProbability("genes to mutate");
        private String NAME;

        ProbabilityType(String name){
            this.NAME = name;
        }

        @Override
        public String toString() {
            return this.NAME;
        }
    }

    @Property(name = "mutation probability type")
    protected ProbabilityType probabilityType = ProbabilityType.ExactProbability;

    @Property(name = "genes to mutate", description = "number of intended genes to be mutated")
    @Range(from = 0, to = Integer.MAX_VALUE)
    protected int genesToMutate = 1;

    @Override
    protected void reproduceInternal(Individual[] children, Individual[] parents) {
        if (parents.length > 0){
            this.computeMutationProbability(parents[0]);
        }
        // for every child
        for (int i = 0; i < children.length; i++){
            // don't forget to set parent fitness!!!
            children[i].setParentFitness(parents[i].getFitness());

            GenotypeRepresentation representation = (GenotypeRepresentation)children[i].getRepresentation();
            for (int j = 0; j < representation.getTotalLength(); j++){
                if (this.randomGenerator.nextRandom() <= this.mutationProbability){
                    representation.invertGeneAt(j);
                    // if not in bounds then invert again
                    if (!function.inBounds(children[i].getCurrentPosition())){
                        representation.invertGeneAt(j);
                    }
                }
            }
        }
    }

    /**
     * Computes exact probability if fractional probability type has been set.
     * @param individual - individual for which the mutation probability should be computed.
     */
    protected void computeMutationProbability(Individual individual){
        if (this.probabilityType == ProbabilityType.FractionalProbability && individual.getRepresentation() != null){
            int totalLength = ((GenotypeRepresentation)individual.getRepresentation()).getTotalLength();
            if (this.genesToMutate >= totalLength){
                this.mutationProbability = 1.0;
            } else {
                this.mutationProbability = (double)this.genesToMutate / totalLength;
            }
        }
    }

    public Class<? extends Representation> getAcceptableType() {
        return GenotypeRepresentation.class;
    }

    public ProbabilityType getProbabilityType() {
        return this.probabilityType;
    }

    public PropertyState getMutationProbabilityState() {
        return this.probabilityType == ProbabilityType.ExactProbability ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public int getGenesToMutate() {
        return this.genesToMutate;
    }

    public PropertyState getGenesToMutateState() {
        return this.probabilityType == ProbabilityType.FractionalProbability ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public void setProbabilityType(ProbabilityType probabilityType) {
        this.probabilityType = probabilityType;
    }

    public void setGenesToMutate(int genesToMutate) {
        this.genesToMutate = genesToMutate;
    }
}
