/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.controller;

import cz.cvut.felk.cig.jcool.experiment.ExperimentRunner;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRunner.State;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author ytoh
 */
public class ReplayExperimentAction extends AbstractAction {
    private ExperimentRunner experimentRunner;

    public void setExperimentRunner(ExperimentRunner experimentRunner) {
        this.experimentRunner = experimentRunner;
    }

    public ReplayExperimentAction() {
        super("Replay experiment");
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            setEnabled(experimentRunner.getExperimentState() == State.FINISHED);
        }
    }

    public void actionPerformed(ActionEvent e) {
        // ignore for now
    }
}
