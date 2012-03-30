/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.controller;

import cz.cvut.felk.cig.jcool.experiment.BasicExperimentRunner;
import cz.cvut.felk.cig.jcool.ui.model.Visualization;
import cz.cvut.felk.cig.jcool.ui.view.IterationVisualization;
import cz.cvut.felk.cig.jcool.ui.view.VisualizationControls;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.apache.commons.lang.SystemUtils;
import org.ytoh.configurations.PropertyState;

/**
 *
 * @author ytoh
 */
public class StartExperimentAction extends AbstractAction implements PropertyChangeListener {
    private static final String STOPPED_NAME = "Start";

    public static final String COMMAND_KEY = StartExperimentAction.class + " [toggle]";

    private BasicExperimentRunner experimentRunner;

    private JPanel primaryVisualizationPanel;
    private JPanel secondaryVisualizationPanel;
    private JPanel telemetryPanel;

    public void setPrimaryVisualizationPanel(JPanel primaryVisualizationPanel) {
        this.primaryVisualizationPanel = primaryVisualizationPanel;
    }

    public void setSecondaryVisualizationPanel(JPanel secondaryVisualizationPanel) {
        this.secondaryVisualizationPanel = secondaryVisualizationPanel;
    }

    public void setTelemetryPanel(JPanel telemetryPanel) {
        this.telemetryPanel = telemetryPanel;
    }

    private Visualization visualization;
    private VisualizationControls visualizationControls;

    public void setVisualization(Visualization visualizationControls) {
        this.visualization = visualizationControls;
    }

    public void setVisualizationControls(VisualizationControls visualizationControls) {
        this.visualizationControls = visualizationControls;
    }

    public StartExperimentAction() {
        super(STOPPED_NAME);
        super.putValue(AbstractAction.ACTION_COMMAND_KEY, COMMAND_KEY);

        if(SystemUtils.IS_OS_MAC) {
            super.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.META_DOWN_MASK));
        } else {
            super.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        }
        this.enabled = false;
    }

    public void setExperimentRunner(BasicExperimentRunner experimentRunner) {
        this.experimentRunner = experimentRunner;
        experimentRunner.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if("state".equals(evt.getPropertyName())) {
            setEnabled(experimentRunner.canStartExperiment());
        }
    }

    public void actionPerformed(ActionEvent e) {
        cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization primaryVisualization = visualization.getPrimaryVisualization();
        if(primaryVisualization != null) {
            primaryVisualization.attachTo(primaryVisualizationPanel);
            experimentRunner.addVisualization(primaryVisualization);
        }

        cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization secondaryVisualization = visualization.getSecondaryVisualization();
        if(secondaryVisualization != null) {
            secondaryVisualization.attachTo(secondaryVisualizationPanel);
            experimentRunner.addVisualization(secondaryVisualization);
        }

        IterationVisualization iterationVisualization = new IterationVisualization();
        iterationVisualization.attachTo(telemetryPanel);
        experimentRunner.addVisualization(iterationVisualization);

        visualization.setPrimaryVisualizationState(PropertyState.DISABLED);
        visualization.setSecondaryVisualizationState(PropertyState.DISABLED);
        visualizationControls.initComponents();
        experimentRunner.startExperiment();
    }
}
