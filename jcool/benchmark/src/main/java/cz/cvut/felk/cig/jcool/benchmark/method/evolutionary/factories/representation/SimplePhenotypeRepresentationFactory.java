package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.factories.representation;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.representations.phenotype.SimplePhenotypeRepresentation;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.RepresentationFactory;
import org.ytoh.configurations.annotations.Component;

/**
 * Created by IntelliJ IDEA.
 * User: miklamar
 * Date: 7.2.2011
 * Time: 15:21:31
 * Factory for SimplePhenotypeRepresentation instances.
 */
@Component(name = "SimplePhenotypeRepresentation factory", description = "Factory responsible for creation of SimplePhenotypeRepresentation instances")
public class SimplePhenotypeRepresentationFactory implements RepresentationFactory{

    Function function;

    public void setFunction(ObjectiveFunction function) {
        this.function = function;
    }

    public Representation createRepresentation(double[] position) {
        Representation representation =  new SimplePhenotypeRepresentation();
        representation.setDoubleValue(position);
        return representation;
    }

    public Class<? extends Representation> getRepresentationType() {
        return SimplePhenotypeRepresentation.class;
    }
}
