package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.representation;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.RepresentationFactory;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 11.3.2011
 * Time: 14:50
 * Core implementation responsible only for core parameter settings.
 */
public abstract class AbstractGenotypeRepresentationFactory implements RepresentationFactory{

    public enum EncodingType {
        FixedPrecision("Fixed precision of variable"), FixedLength("Fixed genes per variable");
        private String NAME;

        private EncodingType(String name){
            NAME = name;
        }

        @Override
        public String toString() {
            return this.NAME;
        }
    }

    @Property(name = "Coding option")
    protected EncodingType encodingType = EncodingType.FixedLength;

    @Property(name = "Fixed precision of variable", description = "sets step size when changing variable value")
    @Range(from = Double.MIN_VALUE, to = Double.MAX_VALUE)
    protected double fixedPrecision = 0.1;

    @Property(name = "Fixed genes per variable", description = "sets how many genes will be used for encoding each variable")
    @Range(from = 1, to = Integer.MAX_VALUE)
    protected int fixedLength = 32;

    public EncodingType getEncodingType() {
        return this.encodingType;
    }

    public double getFixedPrecision() {
        return this.fixedPrecision;
    }

    public PropertyState getFixedPrecisionState() {
        return (this.encodingType == EncodingType.FixedPrecision) ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public int getFixedLength() {
        return this.fixedLength;
    }

    public PropertyState getFixedLengthState() {
        return (this.encodingType == EncodingType.FixedLength) ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public void setEncodingType(EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    public void setFixedPrecision(double fixedPrecision) {
        this.fixedPrecision = fixedPrecision;
    }

    public void setFixedLength(int fixedLength) {
        this.fixedLength = fixedLength;
    }
}
