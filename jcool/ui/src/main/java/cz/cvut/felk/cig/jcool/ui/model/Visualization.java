/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.model;

import cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.context.MutableContext;
import org.ytoh.configurations.ui.DynamicDropDown;
import org.ytoh.configurations.ui.DynamicDropDown.Label;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 *
 * @author ytoh
 */
@Component(name="Visualization")
public class Visualization implements PropertyChangeListener{

    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    private MutableContext context;

    private ExperimentSetup setup;

    @Property(name="Show telemetry:")
    private boolean showTelemetry = true;

    /////////////////////////////////
    // Primary visualization
    /////////////////////////////////
    @Property(name="Primary:")
    @DynamicDropDown(type=cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization.class, key="visualizations", label = Label.NAME)
    private cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization primaryVisualization;

    private PropertyState primaryVisualizationState = PropertyState.ENABLED;
    
    public void setPrimaryVisualizationState(PropertyState primaryVisualizationState) {
        this.primaryVisualizationState = primaryVisualizationState;
    }

    public PropertyState getPrimaryVisualizationState() {
        return primaryVisualizationState;
    }

    public cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization getPrimaryVisualization() {
        return primaryVisualization;
    }

    public void setPrimaryVisualization(cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization primaryVisualization) {
        this.primaryVisualization = primaryVisualization;
    }

    /////////////////////////////////
    // Secondary visualization
    /////////////////////////////////
    @Property(name="Secondary:")
    @DynamicDropDown(type=cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization.class, key="visualizations", label = Label.NAME)
    private cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization secondaryVisualization;

    private PropertyState secondaryVisualizationState = PropertyState.ENABLED;

    public void setSecondaryVisualizationState(PropertyState secondaryVisualizationState) {
        this.secondaryVisualizationState = secondaryVisualizationState;
    }

    public PropertyState getSecondaryVisualizationState() {
        return secondaryVisualizationState;
    }

    public cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization getSecondaryVisualization() {
        return secondaryVisualization;
    }

    public void setSecondaryVisualization(cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization secondaryVisualization) {
        this.secondaryVisualization = secondaryVisualization;
    }

    @Property(name="View type:")
    private Type visualizationType = Type.PRIMARY_ONLY;

    public static enum Type {
        NONE("No display"), PRIMARY_ONLY("Primary only"), SECONDARY_ONLY("Secondary only"), FAVOR_PRIMARY("Favor primary"), EQUAL("Equal display");

        private final String NAME;
        private Type(String name) {
            NAME = name;
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    public boolean isShowTelemetry() {
        return showTelemetry;
    }

    public void setShowTelemetry(boolean showTelemetry) {
        boolean old = this.showTelemetry;
        this.showTelemetry = showTelemetry;
        support.firePropertyChange("show-telemetry", old, showTelemetry);
    }

    public Type getVisualizationType() {
        return visualizationType;
    }

    public void setVisualizationType(Type visualizationType) {
        Type old = this.visualizationType;
        this.visualizationType = visualizationType;
        support.firePropertyChange("type", old, visualizationType);
    }

    //////////////////////////////
    // Context dependent actions
    //////////////////////////////

    /**
     * Sets current context for extraction of "visualizations" list.
     * @param context - Context that might have registered "visualizations" key.
     */
    public void setContext(MutableContext context) {
        this.context = context;
    }

    /**
     * Registers this object as PropertyChangeListener of given ExperimentSetup.
     * @param setup - ExperimentSetup that is currently instantiated.
     */
    public void setExperimentSetup(ExperimentSetup setup){
        // if any, then remove from previous ExperimentSetup from PropertyChangeListeners
        if (this.setup != null){
            this.setup.removePropertyChangeListener(this);
        }
        this.setup = setup;
        this.setup.addPropertyChangeListener(this);
    }

    /**
     * Responds on "visualizations" change event and discards selected visualizations that are no longer accessible.
     * @param evt - PropertyChangeEvent that might changed "visualizations" property.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("visualizations") && this.context != null) {
            List<? extends TelemetryVisualization> visualizations = this.context.getList(TelemetryVisualization.class, "visualizations");
            if (visualizations == null){
                // ensure property change fire
                this.setPrimaryVisualization(null);
                this.setSecondaryVisualization(null);
            } else {
                // if selected visualizations are not in visualizations list, then make them initial
                if (this.primaryVisualization != null && !visualizations.contains(this.primaryVisualization)){
                    this.setPrimaryVisualization(null);
                }
                if (this.secondaryVisualization != null && !visualizations.contains(this.secondaryVisualization)){
                    this.setSecondaryVisualization(null);
                }
            }
        }
    }

    //////////////////////////////
    // Property change support
    //////////////////////////////
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }
}
