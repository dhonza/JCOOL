/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.controller;

import cz.cvut.felk.cig.jcool.experiment.BasicExperimentRunner;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRunner.State;
import cz.cvut.felk.cig.jcool.ui.view.ResultsDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.apache.commons.lang.SystemUtils;

/**
 *
 * @author ytoh
 */
public class ShowResultsAction extends AbstractAction implements PropertyChangeListener {

    private ResultsDialog resultsDialog;

    public ShowResultsAction() {
        super("Show results");
        setEnabled(false);
        if(SystemUtils.IS_OS_MAC) {
            super.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.META_DOWN_MASK));
        } else {
            super.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        }
    }

    public void setResultsDialog(ResultsDialog resultsDialog) {
        this.resultsDialog = resultsDialog;
    }

    public void setExperimentRunner(BasicExperimentRunner experimentRunner) {
        experimentRunner.addPropertyChangeListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                resultsDialog.display();
            }
        });
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            setEnabled(evt.getNewValue() == State.FINISHED);
        }
    }
}
