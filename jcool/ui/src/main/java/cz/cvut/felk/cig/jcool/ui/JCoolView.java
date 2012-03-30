/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui;

import cz.cvut.felk.cig.jcool.ui.*;
import cz.cvut.felk.cig.jcool.ui.util.ViewUtils;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 *
 * @author ytoh
 */
public class JCoolView {
    private JFrame frame;
    private String title;
    private JExpandList functionList;
    private JExpandList methodList;
    private JCoolController controller;

    // shared actions
//    private NewExperimentAction newExperimentAction;
//    private BatchExperimentAction batchExperimentAction;

//    public void setBatchExperimentAction(BatchExperimentAction batchExperimentAction) {
//        this.batchExperimentAction = batchExperimentAction;
//    }

//    public void setNewExperimentAction(NewExperimentAction newExperimentAction) {
//        this.newExperimentAction = newExperimentAction;
//    }

    public void setController(JCoolController controller) {
        this.controller = controller;
    }

    public void setFunctionList(JExpandList functionList) {
        this.functionList = functionList;
    }

    public void setMethodList(JExpandList methodList) {
        this.methodList = methodList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void init() {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JScrollPane functionScrollPane = new JScrollPane();
        JScrollPane methodScrollPane = new JScrollPane();
        functionScrollPane.setViewportView(functionList);
        methodScrollPane.setViewportView(methodList);
        methodList.addMouseListener(new MethodDetailListener());
        functionList.addMouseListener(new FunctionDetailListener());

        JPanel toolBar = new JPanel();
        toolBar.setLayout(new BorderLayout());
        JButton newExperimentButton = new JButton("");
        newExperimentButton.setBorderPainted(false);
        toolBar.add(newExperimentButton, BorderLayout.WEST);

        toolBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JPanel statusBar = new JPanel();
        statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        GroupLayout layout = new org.jdesktop.layout.GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.HORIZONTAL)
                .add(toolBar)
                .add(layout.createSequentialGroup()
                    .add(functionScrollPane, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.UNRELATED)
                    .add(methodScrollPane, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(statusBar));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .add(toolBar, 25, 25, 25)
                .add(layout.createParallelGroup(GroupLayout.VERTICAL)
                    .add(functionScrollPane, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(methodScrollPane, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(statusBar, 25, 25, 25));

        frame.setJMenuBar(createMenuBar());

        frame.setResizable(true);

        frame.pack();
        ViewUtils.centerOnScreen(frame);
    }

    private JMenuBar createMenuBar() {
        JMenuItem exitMI = new JMenuItem("Exit");
        if(System.getProperty("os.name").startsWith("Mac")) {
            exitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.META_MASK));
        } else {
            exitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        }
        exitMI.setMnemonic(KeyEvent.VK_X);
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(exitMI);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem startExperimentMI = new JMenuItem("");
        JMenuItem batchExperimentMI = new JMenuItem("");
        JMenuItem benchmarkMI = new JMenuItem("Benchamrk method");
        JMenu experimentMenu = new JMenu("Experiment");
        experimentMenu.add(startExperimentMI);
        experimentMenu.add(batchExperimentMI);
        experimentMenu.add(benchmarkMI);
        JMenuItem aboutMI = new JMenuItem("About");
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(aboutMI);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(experimentMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }


    // private listeners
    private class MethodDetailListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                controller.displayMethodDetail(methodList.rowAtPoint(e.getPoint()));
            }
        }
    }

    private class FunctionDetailListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                controller.displayFunctionDetail(functionList.rowAtPoint(e.getPoint()));
            }
        }
    }
}
