package cz.cvut.felk.cig.jcool.benchmark.method.evolutionary;

import cz.cvut.felk.cig.jcool.core.ObjectiveFunction;

/**
 * Created by IntelliJ IDEA.
 * User: SuperLooser
 * Date: 28.1.2011
 * Time: 20:33:09
 * Factory responsible for creation of proper Representation instances.
 * Very complex for genotype representations which have multiple genes with possible multiple lengths, ranges and precissions.
 * Resulting representation instance should be able to decode itself into human representation, i.e. double[].
 */
public interface RepresentationFactory {
    /**
     * Extracts core representation parameters, e.g. number of variables, variables bounds.
     * Rest of parameter are either set implicitly or through getters and setters of concrete implementation.
     * Has to be called after function has properly set all its parameters, e.g. variable count,...
     * @param function - ObjectiveFunction for which the representation will create appropriate descriptor (number of genes, bounds, precision) to pass to Representation instance. 
     */
    void setFunction(ObjectiveFunction function);

    /**
     * Encodes given position into actual Representation according to previously set function and parameters.  
     * @param position - n-dimensional point to be encoded into current Representation.
     * @return Representation of given position.
     */
    Representation createRepresentation(double[] position);

    /**
     * Returns concrete Representation type that this factory manufactures.
     * @return
     */
    Class<? extends Representation> getRepresentationType();
}
