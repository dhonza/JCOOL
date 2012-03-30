/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.experiment.BasicExperimentRunner;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRunner.State;
import cz.cvut.felk.cig.jcool.ui.controller.SaveResultsAction;
import cz.cvut.felk.cig.jcool.ui.util.ViewUtils;
import org.apache.commons.lang.SystemUtils;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.ytoh.configurations.PropertyState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author ytoh
 */
public class ResultsDialog extends JDialog implements PropertyChangeListener {

    private BasicExperimentRunner experimentRunner;

    private JPanel  holder;
    private JButton saveButton;
    private JButton cancelButton;
    // Actions
    private SaveResultsAction saveResultsAction;
    private cz.cvut.felk.cig.jcool.ui.model.Visualization visualization;
    private VisualizationControls visualizationControls;

    public ResultsDialog(Experiment experiment, String title, boolean modal, BasicExperimentRunner experimentRunner, SaveResultsAction saveResultsAction, cz.cvut.felk.cig.jcool.ui.model.Visualization visualization, VisualizationControls visualizationControls) {
        super(experiment, title, modal);
        this.experimentRunner = experimentRunner;
        experimentRunner.addPropertyChangeListener(this);
        this.saveResultsAction = saveResultsAction;
        initComponents();
        this.visualization = visualization;
        this.visualizationControls = visualizationControls;
    }

    private void initComponents() {
        holder = new JPanel();
        holder.setLayout(new BorderLayout());
        holder.add(new JLabel("No results available"), BorderLayout.CENTER);
        saveButton = new JButton(saveResultsAction);
        cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        // enable visualizations after closing the window
                        visualization.setPrimaryVisualizationState(PropertyState.ENABLED);
                        visualization.setSecondaryVisualizationState(PropertyState.ENABLED);
                        visualizationControls.initComponents();
                        setVisible(false);
                    }
                });
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.TRAILING)
                    .add(holder, GroupLayout.LEADING, GroupLayout.DEFAULT_SIZE, 800)
                    .add(layout.createSequentialGroup()
                        .add(saveButton)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
//                .addPreferredGap(8, 8, 8)
                .add(holder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(saveButton))
                .addContainerGap())
        );

        setResizable(false);
    }

    public void display() {
        if(experimentRunner.getExperimentState() == State.FINISHED) {

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    saveResultsAction.init(ResultsDialog.this, new JFileChooser(SystemUtils.getUserDir()), experimentRunner.getExperimentResults());
                    holder.removeAll();
                    holder.add(new ResultsOverview(experimentRunner.getExperimentResults()), BorderLayout.CENTER);
                    ViewUtils.centerOnComponent(ResultsDialog.this, getOwner());
                    pack();
                    setVisible(true);
                }
            });
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            display();
        }
    }
}
