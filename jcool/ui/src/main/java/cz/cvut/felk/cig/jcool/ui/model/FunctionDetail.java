/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.model;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.FunctionBounds;
import cz.cvut.felk.cig.jcool.core.FunctionGradient;
import cz.cvut.felk.cig.jcool.core.FunctionHessian;
import org.apache.log4j.Logger;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.util.AnnotationPropertyExtractor;

/**
 *
 * @author ytoh
 */
public class FunctionDetail {
    static final Logger logger = Logger.getLogger(FunctionDetail.class);

    private Function        function;
    private String          name;
    private String          description;
    private String          shortDescription;
    private boolean         hasAnalyticalGradient;
    private boolean         hasAnalyticalHessian;
    private boolean         isBound;
    private FunctionBounds  bounds;
    private String          icon;
    private Property[]      parameters = new Property[0];

    public FunctionDetail(Function function) {
        this.function = function;

        parameters = new AnnotationPropertyExtractor().propertiesFor(function).toArray(new Property[0]);

        hasAnalyticalGradient = function instanceof FunctionGradient;
        hasAnalyticalHessian  = function instanceof FunctionHessian;
        isBound               = function instanceof FunctionBounds;

        if(isBound) {
            bounds = ((FunctionBounds)function);
        }
        
        if(function.getClass().isAnnotationPresent(Component.class)) {
            Component component = function.getClass().getAnnotation(Component.class);
            name = component.name();
            description = component.description();
            shortDescription = component.shortDescription();
        } else {
            name = function.getClass().getSimpleName();
            description = shortDescription = "No description available";
        }

        icon = "Zebra.png";
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Function getFunction() {
        return function;
    }

    public boolean isHasAnalyticalGradient() {
        return hasAnalyticalGradient;
    }

    public boolean isHasAnalyticalHessian() {
        return hasAnalyticalHessian;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getDimension() {
        return function.getDimension();
    }

    public double getMinimum(int dimension) {
        if(dimension < 0 || dimension >= getDimension()) {
            throw new ArrayIndexOutOfBoundsException(String.format("dimension parameter out of bounds: %d", dimension));
        }

        if(isBound) {
            return bounds.getMinimum()[dimension];
        }

        return Double.NEGATIVE_INFINITY;
    }

    public double getMaximum(int dimension) {
        if(dimension < 0 || dimension >= getDimension()) {
            throw new ArrayIndexOutOfBoundsException(String.format("dimension parameter out of bounds: %d", dimension));
        }

        if(isBound) {
            return bounds.getMaximum()[dimension];
        }

        return Double.POSITIVE_INFINITY;
    }

    public Property[] getProperties() {
        return parameters;
    }

    @Override
    public String toString() {
        return name;
    }
}
