package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.representation;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.GenotypeRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.BitSetBinaryRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.genotype.BitSetGrayCodeBinaryRepresentation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 14.4.2011
 * Time: 12:24
 * GenotypeFactory mostly the same as SimpleGenotypeRepresentationFactory except this one creates instances of BitSetBinaryRepresentation and BitSetGrayCodeBinaryRepresentation
 */
@Component(name = "BitSet genotype representation factory", description = "Factory responsible for creation of BitSetBinaryRepresentation and BitSetGrayCodeBinaryRepresentation instances")
public class BitSetGenotypeRepresentationFactory extends SimpleGenotypeRepresentationFactory {

    @Override
    protected GenotypeRepresentation createBinaryRepresentation() {
        return new BitSetBinaryRepresentation(this.descriptor);
    }

    @Override
    protected GenotypeRepresentation createGrayCodeRepresentation() {
        return new BitSetGrayCodeBinaryRepresentation(this.descriptor);
    }

    @Override
    public Class<? extends Representation> getRepresentationType() {
        return BitSetBinaryRepresentation.class;
    }
}
