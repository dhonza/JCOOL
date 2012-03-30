/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.ui.controller.HideControlsAction;
import cz.cvut.felk.cig.jcool.ui.controller.HideLogAction;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;

/**
 *
 * @author ytoh
 */
public class Experiment extends JFrame implements PropertyChangeListener {
    static Logger logger = Logger.getLogger(Experiment.class);

    // composition
    private JPanel visualizationPanel;
    private JPanel settingsPanel;
    private JPanel logPanel;

    // action
    private Action hideControlsAction;
    private Action hideLogAction;
    // actions
    private Action startExperimentAction;
    private Action stopExperimentAction;
    private Action resetExperimentAction;
    private Action showResultsAction;
    private Action replayExperimentAction;

    // convenience shortcut
    private boolean displayLog = true;
    private boolean displayControls = true;
    private JSplitPane horizontal;
    private JSplitPane vertical;

    private static final int HORIZONTAL_SPLIT_POSITION = 500;
    private static final int VERTICAL_SPLIT_POSITION = 540;

    public void setLogPanel(JPanel logPanel) {
        this.logPanel = logPanel;
    }

    public void setSettingsPanel(JPanel settingsPanel) {
        this.settingsPanel = settingsPanel;
    }

    public void setVisualizationPanel(JPanel visualizationPanel) {
        this.visualizationPanel = visualizationPanel;
    }

    public void setHideControlsAction(Action hideControlsAction) {
        this.hideControlsAction = hideControlsAction;
        hideControlsAction.addPropertyChangeListener(this);
    }

    public void setHideLogAction(Action hideLogAction) {
        this.hideLogAction = hideLogAction;
        hideLogAction.addPropertyChangeListener(this);
    }

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

    /**
     * 
     */
    public void initComponents() {
        visualizationPanel.setBorder(BorderFactory.createEtchedBorder());
        settingsPanel.setBorder(BorderFactory.createEtchedBorder());
        logPanel.setBorder(BorderFactory.createEtchedBorder());

        setJMenuBar(createMenuBar());

        createMovableLayout();

        pack();

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                horizontal.setDividerLocation((int)(e.getComponent().getSize().getWidth() - settingsPanel.getMinimumSize().getWidth() - 15));
                validate();
            }
        });
    }

    private void createMovableLayout() {
        visualizationPanel.setPreferredSize(new Dimension(HORIZONTAL_SPLIT_POSITION, VERTICAL_SPLIT_POSITION));
        settingsPanel.setPreferredSize(new Dimension(300, 680));
        logPanel.setPreferredSize(new Dimension(HORIZONTAL_SPLIT_POSITION, 140));

        getContentPane().setLayout(new BorderLayout());
        horizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);

        horizontal.setDividerLocation(HORIZONTAL_SPLIT_POSITION);
        vertical.setDividerLocation(VERTICAL_SPLIT_POSITION);

        vertical.setTopComponent(visualizationPanel);
        vertical.setBottomComponent(logPanel);
        horizontal.setLeftComponent(vertical);
        horizontal.setRightComponent(settingsPanel);
        getContentPane().add(horizontal, BorderLayout.CENTER);
    }

    /**
     *
     */
    private void createLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(horizontal(layout)));
        layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(vertical(layout)));
    }

    /**
     *
     * @param layout
     * @return
     */
    private SequentialGroup horizontal(GroupLayout layout) {
        SequentialGroup horizontalGroup = layout.createSequentialGroup();
        horizontalGroup.addContainerGap();

        if(displayLog) {
            horizontalGroup.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(visualizationPanel, 500, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .add(logPanel, 500, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));
        } else {
            horizontalGroup.add(visualizationPanel, 500, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
        }

        if(displayControls) {
            horizontalGroup.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED);
            horizontalGroup.add(settingsPanel, 300, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE);
        }

        horizontalGroup.addContainerGap();
        return horizontalGroup;
    }

    /**
     *
     * @param layout
     * @return
     */
    private SequentialGroup vertical(GroupLayout layout) {
        SequentialGroup verticalSeq = layout.createSequentialGroup();
        verticalSeq.addContainerGap();

        if(displayControls) {
            ParallelGroup parallel = layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING);
            SequentialGroup sequential = layout.createSequentialGroup();

            parallel.add(settingsPanel, 680, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
            sequential.add(visualizationPanel, 540, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);

            if(displayLog) {
                sequential.addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED);
                sequential.add(logPanel, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE);
            }

            verticalSeq.add(parallel.add(sequential));
        } else {

            SequentialGroup sequential = layout.createSequentialGroup();
            sequential.add(visualizationPanel, 540, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);

            if(displayLog) {
                sequential.addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED);
                sequential.add(logPanel, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE);
            }

            verticalSeq.add(sequential);
        }

        verticalSeq.addContainerGap();
        return verticalSeq;
    }

    /**
     *
     * @return
     */
    private JMenuBar createMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        file.add(exit);
        JMenu view = new JMenu("View");
        JMenuItem showControls = new JMenuItem(hideControlsAction);
        JMenuItem showLog = new JMenuItem(hideLogAction);
        view.add(showControls);
        view.add(showLog);
        JMenu experiment = new JMenu("Experiment");
        experiment.add(startExperimentAction);
        experiment.add(stopExperimentAction);
        experiment.add(resetExperimentAction);
        experiment.add(showResultsAction);
        experiment.add(replayExperimentAction);
        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        help.add(about);
        menubar.add(file);
        menubar.add(experiment);
        menubar.add(view);
        menubar.add(help);
        return menubar;
    }

    /**
     *
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if(HideLogAction.COMMAND_KEY.equals(evt.getPropertyName())) {
            displayLog = !(Boolean)evt.getNewValue();
            logPanel.setVisible(displayLog);
            if(vertical != null) {
                if(displayLog) {
                    vertical.setDividerLocation(vertical.getLastDividerLocation());
                } else {
                    vertical.setDividerLocation(vertical.getMaximumDividerLocation());
                }
            }

//            createLayout();
            validate();
            repaint();
        }

        if(HideControlsAction.COMMAND_KEY.equals(evt.getPropertyName())) {
            displayControls = !(Boolean)evt.getNewValue();
            settingsPanel.setVisible(displayControls);
            if(horizontal != null) {
                if(displayControls) {
                    horizontal.setDividerLocation(horizontal.getLastDividerLocation());
                } else {
                    horizontal.setDividerLocation(horizontal.getMaximumDividerLocation());
                }
            }

//            createLayout();
            validate();
            repaint();
        }
    }
}
