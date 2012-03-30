package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.util.MathUtils;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 7.2.2011
 * Time: 15:41:53
 * Simple implementation of GenomeRepresentation holding its content in boolean array.
 */
public class SimpleBinaryRepresentation implements GenotypeRepresentation{

    /**
     * encoded internal data holder.
     */
    protected boolean[] genome = new boolean[0];

    /**
     * Descriptor used for encoding and decoding of internal data.
     */
    protected SimpleBinaryGenomeDescriptor descriptor;

    public SimpleBinaryRepresentation(SimpleBinaryGenomeDescriptor descriptor){
        this.descriptor = descriptor;
        this.genome = new boolean[MathUtils.sumSizes(descriptor.getVariablesLengths())];
    }

    public SimpleBinaryRepresentation(SimpleBinaryRepresentation second){
        this.genome = Arrays.copyOf(second.genome, second.genome.length);
        this.descriptor = second.descriptor;
    }

    public int getTotalLength() {
        return genome.length;
    }

    public int[] getValueLengths() {
        return Arrays.copyOf(descriptor.getVariablesLengths(), descriptor.getVariablesLengths().length);
    }

    public boolean getGeneAt(int idx) {
        if (idx < 0 || idx > genome.length){
            throw new ArrayIndexOutOfBoundsException(this.getClass().getSimpleName() + ": cannot obtain gene at " + idx + " because genome length is " + genome.length);
        }
        return genome[idx];
    }

    public void setGeneAt(int idx, boolean value){
        if (idx < 0 || idx > genome.length){
            throw new ArrayIndexOutOfBoundsException(this.getClass().getSimpleName() + ": cannot set gene at " + idx + " because genome length is " + genome.length);
        }
        this.genome[idx] = value;
    }

    public void invertGeneAt(int idx) {
        if (idx < 0 || idx > genome.length){
            throw new ArrayIndexOutOfBoundsException(this.getClass().getSimpleName() + ": cannot invert gene at " + idx + " because genome length is " + genome.length);
        }
        genome[idx] = !genome[idx];
    }

    public void swapGenes(GenotypeRepresentation secondRepresentation, int[] positions) {
        for (int position : positions){
            swapGenesInternal(secondRepresentation, position);
        }
    }

    public void swapGenes(GenotypeRepresentation secondRepresentation, int from, int to) {
        for (int position = from; position < to; position++){
            swapGenesInternal(secondRepresentation, position);
        }
    }

    /**
     * Swaps gene values between this and given representation instance.
     * Usage of this method is performance killer but is clean from design point of view (Code once, use everywhere).
     * @param secondRepresentation - representation of second Individual to be processed.
     * @param position - position which will be processed.
     */
    protected void swapGenesInternal(GenotypeRepresentation secondRepresentation, int position){
        boolean tmp;
        tmp = secondRepresentation.getGeneAt(position);
        secondRepresentation.setGeneAt(position, this.genome[position]);
        this.genome[position] = tmp;
    }

    public void receiveGenes(GenotypeRepresentation secondRepresentation, int[] positions) {
        for (int position : positions){
            this.genome[position] = secondRepresentation.getGeneAt(position);
        }
    }

    public void receiveGenes(GenotypeRepresentation secondRepresentation, int from, int to) {
        for (int position = from; position < to; position++){
            this.genome[position] = secondRepresentation.getGeneAt(position);
        }
    }

    public void andGenes(GenotypeRepresentation secondRepresentation, int[] positions) {
        for (int position : positions){
            this.genome[position] &= secondRepresentation.getGeneAt(position);
        }
    }

    public void andGenes(GenotypeRepresentation secondRepresentation, int from, int to) {
        for (int position = from; position < to; position++){
            this.genome[position] &= secondRepresentation.getGeneAt(position);
        }
    }

    public void orGenes(GenotypeRepresentation secondRepresentation, int[] positions) {
        for (int position : positions){
            this.genome[position] |= secondRepresentation.getGeneAt(position);
        }
    }

    public void orGenes(GenotypeRepresentation secondRepresentation, int from, int to) {
        for (int position = from; position < to; position++){
            this.genome[position] |= secondRepresentation.getGeneAt(position);
        }
    }

    public void xorGenes(GenotypeRepresentation secondRepresentation, int[] positions) {
        for (int position : positions){
            this.genome[position] ^= secondRepresentation.getGeneAt(position);
        }
    }

    public void xorGenes(GenotypeRepresentation secondRepresentation, int from, int to) {
        for (int position = from; position < to; position++){
            this.genome[position] ^= secondRepresentation.getGeneAt(position);
        }
    }

    public double[] getDoubleValue() {
        double[] intValues = this.binaryToInteger();
        double[] doubleValues = new double[this.descriptor.getNumVariables()];
        for (int i = 0; i < doubleValues.length; i++){
            doubleValues[i] = this.descriptor.getVariablesLowerBounds()[i] + intValues[i] * this.descriptor.getVariablesPrecisions()[i];
        }
        return doubleValues;
    }

    public void setDoubleValue(double[] phenotypeValue) {
        if (this.genome == null || this.genome.length == 0){
            this.genome = new boolean[MathUtils.sumSizes(this.descriptor.getVariablesLengths())];
        }
        double[] intValues = new double[this.descriptor.getNumVariables()];
        for (int i = 0; i < this.descriptor.getNumVariables(); i++){
            // prevent falling out from defined interval
            if (phenotypeValue[i] < this.descriptor.getVariablesLowerBounds()[i]){
                phenotypeValue[i] = this.descriptor.getVariablesLowerBounds()[i];
            }
            intValues[i] = Math.ceil((phenotypeValue[i] - this.descriptor.getVariablesLowerBounds()[i]) / this.descriptor.getVariablesPrecisions()[i]);
        }
        integerToBinary(intValues);
    }

    public Representation copy() {
        return new SimpleBinaryRepresentation(this);
    }

    /**
     * Returns genome variables integer translation.
     * @return genome variables integer translation.
     */
    protected double[] binaryToInteger(){
        double[] intValues = new double[this.descriptor.getNumVariables()];
        boolean[] decodedGenome = this.decodeGenomeToBinary();
        int index = MathUtils.sumSizes(this.descriptor.variablesLengths) - 1; // start from the end
        double intValue;
        double power; // stores power of two
        // for every variable...
        for (int variable = intValues.length - 1; variable >= 0 ; variable--){
            intValue = 0.0;
            power = 1.0;
            // ... extract its value
            for (int gene = 0; gene < this.descriptor.getVariablesLengths()[variable]; gene++){
                intValue += (decodedGenome[index--]?power:0);
                power *= 2;
            }
            intValues[variable] = Math.ceil(intValue); // make it round int value due to accumulated error
        }
        return intValues;
    }

    /**
     * Converts given "intValues" into internal representation
     * @param intValues
     */
    protected void integerToBinary(double[] intValues){
        int maxIndex = -1;
        int minIndex;
        double currentValue;
        double newValue;
        // for every variable...
        for (int variableIdx = 0; variableIdx < intValues.length; variableIdx++){
            currentValue = intValues[variableIdx];
            minIndex = maxIndex; // previous maxIndex is current minIndex
            maxIndex += this.descriptor.getVariablesLengths()[variableIdx]; // move maxIndex forward
            // ... divide its value and place it to appropriate place
            for (int genomeIdx = maxIndex; genomeIdx > minIndex; genomeIdx--){
                newValue = Math.floor(currentValue / 2.0);
                this.genome[genomeIdx] = ( (currentValue - newValue * 2.0) > 0.5);
                currentValue = newValue;
            }
        }
        encodeBinaryGenome();
    }

    /**
     * Decodes genome to traditional binary coding.
     * SimpleBinaryRepresentation does not do anything with binary representation.
     * Suitable for (i.e. gray) ancestors to override.
     * @return genome written in binary coding. 
     */
    protected boolean[] decodeGenomeToBinary(){
        return this.genome;
    }

    /**
     * Transforms genome into different binary form.
     * SimpleBinaryRepresentation does not do anything with binary representation.
     * Suitable for (i.e. gray) ancestors to override.
     */
    protected void encodeBinaryGenome(){
    }
}
