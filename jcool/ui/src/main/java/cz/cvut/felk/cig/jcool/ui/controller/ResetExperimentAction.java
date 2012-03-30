/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.controller;

import cz.cvut.felk.cig.jcool.experiment.BasicExperimentRunner;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRunner.State;
import cz.cvut.felk.cig.jcool.ui.model.Visualization;
import cz.cvut.felk.cig.jcool.ui.view.VisualizationControls;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.apache.commons.lang.SystemUtils;
import org.ytoh.configurations.PropertyState;

/**
 *
 * @author ytoh
 */
public class ResetExperimentAction extends AbstractAction implements PropertyChangeListener {
    public static final String COMMAND_KEY = ResetExperimentAction.class + " [toggle]";

    private BasicExperimentRunner experimentRunner;

    private Visualization visualization;
    private VisualizationControls visualizationControls;

    public void setVisualization(Visualization visualizationControls) {
        this.visualization = visualizationControls;
    }

    public void setVisualizationControls(VisualizationControls visualizationControls) {
        this.visualizationControls = visualizationControls;
    }

    public ResetExperimentAction() {
        super("Reset experiment");
        super.putValue(AbstractAction.ACTION_COMMAND_KEY, COMMAND_KEY);
        if(SystemUtils.IS_OS_MAC) {
            super.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.META_DOWN_MASK));
        } else {
            super.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        }
        setEnabled(false);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if("state".equals(evt.getPropertyName())) {
            setEnabled(experimentRunner.canResetExperiment());
            if(experimentRunner.getExperimentState() == State.FINISHED || experimentRunner.getExperimentState() == State.CANCELLED) {
                visualization.setPrimaryVisualizationState(PropertyState.ENABLED);
                visualization.setSecondaryVisualizationState(PropertyState.ENABLED);
            }
        }
    }

    public void setExperimentRunner(BasicExperimentRunner experimentRunner) {
        this.experimentRunner = experimentRunner;
        experimentRunner.addPropertyChangeListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        visualization.setPrimaryVisualizationState(PropertyState.ENABLED);
        visualization.setSecondaryVisualizationState(PropertyState.ENABLED);
        visualizationControls.initComponents();
        experimentRunner.resetExperiment();
    }
}

