package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.representation;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.SimpleBinaryGenomeDescriptor;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.SimpleBinaryRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.SimpleGrayCodeBinaryRepresentation;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.core.OptimizationException;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 11.3.2011
 * Time: 15:42
 * Factory that creates SimpleBinaryRepresentation instances.
 */
@Component(name = "SimpleGenotypeRepresentationFactory", description = "Factory responsible for creation of SimpleBinaryRepresentation instances")
public class SimpleGenotypeRepresentationFactory extends AbstractGenotypeRepresentationFactory{

    public enum CodeType {
        BinaryCode("Traditional binary code"), GrayCode("Gray code");
        private String NAME;

        private CodeType(String name){
            NAME = name;
        }

        @Override
        public String toString() {
            return this.NAME;
        }
    }

    @Property(name = "Genotype coding type")
    protected CodeType codeType = CodeType.BinaryCode;

    protected ObjectiveFunction function;

    protected SimpleBinaryGenomeDescriptor descriptor = new SimpleBinaryGenomeDescriptor();

    protected static double LN2 = Math.log(2);

    public SimpleGenotypeRepresentationFactory(){}

    /**
     * Main method responsible for creation of appropriate Representation instances.
     * @param position - n-dimensional point to be encoded into current Representation.
     * @return resulting representation instance.
     */
    public Representation createRepresentation(double[] position) {
        GenotypeRepresentation representation;
        if (this.codeType == CodeType.BinaryCode){
            representation = this.createBinaryRepresentation();
        } else{
            representation = this.createGrayCodeRepresentation();
        }
        representation.setDoubleValue(position);
        return representation;
    }

    /**
     * Internal method responsible only for instantiation of representation encoded with traditional binary code.
     * @return representation encoded with traditional binary code.
     */
    protected GenotypeRepresentation createBinaryRepresentation(){
        return new SimpleBinaryRepresentation(this.descriptor);
    }

    /**
     * Internal method responsible only for instantiation of representation encoded with Gray code.
     * @return representation encoded with Gray code.
     */
    protected GenotypeRepresentation createGrayCodeRepresentation(){
        return new SimpleGrayCodeBinaryRepresentation(this.descriptor);
    }

    /**
     * Calculates the internal values for generating the right descriptor.
     * @param function - ObjectiveFunction for which the representation will create appropriate descriptor (number of genes, bounds, precision) to pass to Representation instance.
     */
    public void setFunction(ObjectiveFunction function) {
        this.function = function;
        this.descriptor.setNumVariables(function.getDimension());
        this.descriptor.setVariablesLowerBounds(function.getMinimum());
        // initialize empty data
        this.descriptor.setVariablesLengths(new int[function.getDimension()]);
        this.descriptor.setVariablesPrecisions(new double[function.getDimension()]);
        switch (this.encodingType){
            case FixedLength:
                processFixedLengthEncoding();
                break;
            case FixedPrecision:
                processFixedPrecisionEncoding();
                break;
        }
    }

    /**
     * Sets fixedLength for encoding and calculates precision of each variable.
     */
    protected void processFixedLengthEncoding(){
        if (this.fixedLength <= 0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": fixedLength has to be positive, but the value " + this.fixedLength + " is not");
        }
        double[] precisions = this.descriptor.getVariablesPrecisions();
        int[] lengths = this.descriptor.getVariablesLengths();
        double numPossibilities = Math.pow(2, this.fixedLength) - 1; // minus 1 is important because first "possibility" is 0 * precision!
        for (int i = 0; i < this.descriptor.getNumVariables(); i++){
            // have to calculate all possibilities and choose step size so that the maximum is certainly included in final range -> numPossibilities * calculatedPrecision >= MAX
            // first calculate the overall range that has to be divided
            double range = this.function.getMaximum()[i] - this.function.getMinimum()[i];
            if (range == Double.POSITIVE_INFINITY){
                throw new OptimizationException(this.getClass().getSimpleName() + ": range for coding is infinite. Try restrict function definition scope");
            }
            double precision = range / numPossibilities;
            if (precision == Double.POSITIVE_INFINITY || precision == 0.0){
                throw new ArithmeticException(this.getClass().getSimpleName() + ": definition scope of function variable number " + i + " is too small for given encoding. Try using fixed encoding of shorter length or use fixed precision encoding instead");
            }
            // set precision that even with accumulated error the maximum will be reachable
            while (precision * numPossibilities < range){
                precision += Double.MIN_VALUE;
            }
            precisions[i] = precision;
            lengths[i] = this.fixedLength;
        }
    }

    /**
     * Sets fixedPrecision encoding and counts calculates length of each variable.
     */
    protected void processFixedPrecisionEncoding(){
        if (this.fixedPrecision <= 0.0){
            throw new OptimizationException(this.getClass().getSimpleName() + ": fixedPrecision has to be positive, but the value " + this.fixedPrecision + " is not");
        }
        double[] precisions = this.descriptor.getVariablesPrecisions();
        int[] lengths = this.descriptor.getVariablesLengths();
        for (int i = 0; i < this.descriptor.getNumVariables(); i++){
            double range = this.function.getMaximum()[i] - this.function.getMinimum()[i];
            if (range == Double.POSITIVE_INFINITY){
                throw new OptimizationException(this.getClass().getSimpleName() + ": range for coding is infinite. Try restrict function definition scope");
            }
            if (range < this.fixedPrecision){
                throw new OptimizationException(this.getClass().getSimpleName() + ": range cannot be smaller than fixedPrecision, but " + range + " < " + this.fixedPrecision);
            }
            double numValues = Math.ceil(range / this.fixedPrecision);
            if (numValues == Double.POSITIVE_INFINITY){
                throw new ArithmeticException(this.getClass().getSimpleName() + ": genotype can obtain too many values. Try restrict variable definition scope or increasing fixed precision of function variable " + i);
            }
            int length = (int)Math.ceil(Math.log(numValues) / LN2);
            if (length == Integer.MAX_VALUE){
                throw new ArithmeticException(this.getClass().getSimpleName() + ": genotype can obtain too many values. Try restrict variable definition scope or increasing fixed precision of function variable " + i);
            }
            precisions[i] = this.fixedPrecision;
            lengths[i] = length;
        }
    }

    public Class<? extends Representation> getRepresentationType() {
        return SimpleBinaryRepresentation.class;
    }

    public CodeType getCodeType() {
        return this.codeType;
    }

    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
    }
}
