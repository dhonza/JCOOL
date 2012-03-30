/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.controller;

import cz.cvut.felk.cig.jcool.experiment.ExperimentRun;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRunSaver;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ytoh
 */
public class SaveResultsAction extends AbstractAction {

    static Logger logger = Logger.getLogger(SaveResultsAction.class);
    //
    private JFileChooser chooser;
    //
    private ExperimentRun results;
    //
    private Component parent;

    /**
     *
     */
    public SaveResultsAction() {
        super("Save results");
        setEnabled(false);

        if(SystemUtils.IS_OS_MAC) {
            super.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.META_DOWN_MASK));
        } else {
            super.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        }
    }

    /**
     *
     * @param chooser
     * @param results
     */
    public void init(Component owner, JFileChooser chooser, ExperimentRun results) {
        this.parent = owner;
        this.chooser = chooser;
        this.results = results;
        setEnabled((chooser != null) && (results != null));
    }

    public void actionPerformed(ActionEvent e) {
        if(enabled) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    ExperimentRunSaver saver = new ExperimentRunSaver(results);
                    chooser.setDialogTitle("Save experiment results");
                    chooser.setSelectedFile(new File(saver.getDefaultFilename()));
                    int status = chooser.showSaveDialog(parent);
                    if(status == JFileChooser.APPROVE_OPTION) {
                        try {
                            saver.save(chooser.getSelectedFile());
                            logger.info("Experiment results saved to file: " + chooser.getSelectedFile().getAbsolutePath());
                        } catch (FileNotFoundException ex) {
                            logger.error("Could not save experiment results", ex);
                        }
                    }
                }
            });
        }
    }
}
