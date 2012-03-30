package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import java.util.BitSet;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.4.2011
 * Time: 11:46
 * Simple binary representation stored in BitSet and encoded with Gray code.
 */
public class BitSetGrayCodeBinaryRepresentation extends BitSetBinaryRepresentation {

    public BitSetGrayCodeBinaryRepresentation(SimpleBinaryGenomeDescriptor descriptor) {
        super(descriptor);
    }

    public BitSetGrayCodeBinaryRepresentation(BitSetGrayCodeBinaryRepresentation second) {
        super(second);
    }

    /**
     * Converts internal genome from gray coding into binary coding.
     * @return binary genome representation.
     */
    @Override
    protected BitSet decodeGenomeToBinary() {
        BitSet binaryGenome = new BitSet(this.genomeLength);
        int index = 0; // index in entire genome; stored from MSB to LSB
        for (int variable = 0; variable < this.descriptor.numVariables; variable++){
            binaryGenome.set(index, this.genome.get(index++)); // MSB bit of every variable is preserved
            for (int i = 1; i < this.descriptor.variablesLengths[variable]; i++){
                binaryGenome.set(index, binaryGenome.get(index-1) ^ this.genome.get(index++));
            }
        }
        return binaryGenome;
    }

    /**
     * Encodes binary genome into internal Gray genome.
     */
    @Override
    protected void encodeBinaryGenome() {
        BitSet grayGenome = new BitSet(this.genomeLength);
        int index = 0; // index in all genome
        for (int variable = 0; variable < this.descriptor.numVariables; variable++){
            grayGenome.set(index, this.genome.get(index++)); // MSB bit of every variable is preserved
            for (int i = 1; i < this.descriptor.variablesLengths[variable]; i++){
                grayGenome.set(index, this.genome.get(index-1) ^ this.genome.get(index++));
            }
        }
        this.genome = grayGenome;
    }

    @Override
    public Representation copy() {
        return new BitSetGrayCodeBinaryRepresentation(this);
    }
}
