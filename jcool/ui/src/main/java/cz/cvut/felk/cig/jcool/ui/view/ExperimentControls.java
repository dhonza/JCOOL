/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.view;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

/**
 *
 * @author ytoh
 */
public class ExperimentControls extends JPanel {

    private JButton start;
    private JButton stop;
    private JButton reset;
    private JButton showResults;
    private JButton replay;

    // actions
    private Action startExperimentAction;
    private Action stopExperimentAction;
    private Action resetExperimentAction;
    private Action showResultsAction;
    private Action replayExperimentAction;

    public void setStartExperimentAction(Action startExperimentAction) {
        this.startExperimentAction = startExperimentAction;
    }

    public void setStopExperimentAction(Action stopExperimentAction) {
        this.stopExperimentAction = stopExperimentAction;
    }

    public void setResetExperimentAction(Action resetExperimentAction) {
        this.resetExperimentAction = resetExperimentAction;
    }

    public void setShowResultsAction(Action showResultsAction) {
        this.showResultsAction = showResultsAction;
    }

    public void setReplayExperimentAction(Action replayExperimentAction) {
        this.replayExperimentAction = replayExperimentAction;
    }

    public void initComponents() {
        start = new JButton(startExperimentAction);
        stop = new JButton(stopExperimentAction);
        reset = new JButton(resetExperimentAction);
        showResults = new JButton(showResultsAction);
        replay = new JButton(replayExperimentAction);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, reset, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(start, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 5, Short.MAX_VALUE)
                        .add(stop, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(showResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(replay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(start)
                    .add(stop))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(reset)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(showResults)
                    .add(replay))
                .addContainerGap())
        );
    }
}
