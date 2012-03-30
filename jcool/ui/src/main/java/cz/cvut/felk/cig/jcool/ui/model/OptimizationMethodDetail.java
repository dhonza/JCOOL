/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.model;

import cz.cvut.felk.cig.jcool.core.OptimizationMethod;
import cz.cvut.felk.cig.jcool.core.StopCondition;
import org.apache.log4j.Logger;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.util.AnnotationPropertyExtractor;

/**
 *
 * @author ytoh
 */
public class OptimizationMethodDetail {
    static final Logger logger = Logger.getLogger(OptimizationMethodDetail.class);

    private OptimizationMethod  method;
    private String              name;
    private String              description;
    private String              shortDescription;
    private String              icon;
    private Property[]          parameters = new Property[0];

    public OptimizationMethodDetail(OptimizationMethod method) {
        this.method = method;

        parameters = new AnnotationPropertyExtractor().propertiesFor(method).toArray(new Property[0]);

        if(method.getClass().isAnnotationPresent(Component.class)) {
            Component component = method.getClass().getAnnotation(Component.class);
            name = component.name();
            description = component.description();
            shortDescription = component.shortDescription();
        } else {
            name = method.getClass().getSimpleName();
            description = shortDescription = "No description available";
        }

        icon = "Zebra.png";
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public OptimizationMethod getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public Property[] getProperties() {
        return parameters;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public StopCondition[] getStopConditions() {
        return method.getStopConditions();
    }

    @Override
    public String toString() {
        return name;
    }
}
