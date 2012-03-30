package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:42:14
 * Extends SimpleBinaryRepresentation and adds coding into gray code,
 */
@Component(name = "Extension of Simple binary representation adding gray coding")
public class SimpleGrayCodeBinaryRepresentation extends SimpleBinaryRepresentation {

    public SimpleGrayCodeBinaryRepresentation(SimpleBinaryGenomeDescriptor descriptor) {
        super(descriptor);
    }

    public SimpleGrayCodeBinaryRepresentation(SimpleGrayCodeBinaryRepresentation second) {
        super(second);
    }

    /**
     * Converts internal genome from gray coding into binary coding.
     * @return binary genome representation.
     */
    @Override
    protected boolean[] decodeGenomeToBinary() {
        boolean[] binaryGenome = new boolean[this.genome.length];
        int index = 0; // index in entire genome; stored from MSB to LSB
        for (int variable = 0; variable < this.descriptor.numVariables; variable++){
            binaryGenome[index] = this.genome[index++]; // MSB bit of every variable is preserved
            for (int i = 1; i < this.descriptor.variablesLengths[variable]; i++){
                binaryGenome[index] = binaryGenome[index-1] ^ this.genome[index++];
            }
        }

        return binaryGenome;
    }

    /**
     * Encodes binary genome into internal Gray genome.
     */
    @Override
    protected void encodeBinaryGenome() {
        boolean[] grayGenome = new boolean[this.genome.length];
        int index = 0; // index in all genome
        for (int variable = 0; variable < this.descriptor.numVariables; variable++){
            grayGenome[index] = this.genome[index++]; // MSB bit of every variable is preserved
            for (int i = 1; i < this.descriptor.variablesLengths[variable]; i++){
                grayGenome[index] = this.genome[index-1] ^ this.genome[index++];
            }
        }
        this.genome = grayGenome;
    }

    @Override
    public Representation copy() {
        return new SimpleGrayCodeBinaryRepresentation(this);
    }
}
