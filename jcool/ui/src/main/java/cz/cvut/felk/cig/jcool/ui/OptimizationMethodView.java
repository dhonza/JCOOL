/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui;

import cz.cvut.felk.cig.jcool.ui.model.OptimizationMethodDetail;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.ytoh.configurations.annotations.Component;

/**
 *
 * @author ytoh
 */
public class OptimizationMethodView {

    private JFrame frame;
    private JLabel title;
    private JTextPane fullDescription;
    private JTable visualizations;
    private JTable parameters;
    private JTable stopConditions;

    public JFrame getFrame(OptimizationMethodDetail detail) {
        title.setText(detail.getName());
        fullDescription.setText(detail.getDescription());
        String[][] values = new String[detail.getProperties().length][2];

        for (int i = 0; i < values.length; i++) {
            values[i][0] = detail.getProperties()[i].getName();
            values[i][1] = detail.getProperties()[i].getDescription();
        }

        DefaultTableModel model = new DefaultTableModel(values, new String[] {"Property", "Description"});
        parameters.setModel(model);

        String[][] conditions = new String[detail.getStopConditions().length][2];

        for (int i = 0; i < conditions.length; i++) {
            conditions[i][0] = detail.getStopConditions()[i].getClass().getSimpleName();
            if(detail.getStopConditions()[i].getClass().isAnnotationPresent(Component.class)) {
                Component c = detail.getStopConditions()[i].getClass().getAnnotation(Component.class);
                conditions[i][1] = c.description();
            } else {
                conditions[i][1] = "";
            }
        }

        DefaultTableModel conditionModel = new DefaultTableModel(conditions, new String[] {"Property", "Description"});
        stopConditions.setModel(conditionModel);

        frame.pack();
        return frame;
    }

    public void init() {
        parameters = new JTable();
        parameters.setEnabled(false);
        JLabel      parametersLabel;
        JSeparator  parametersSeparator;
        stopConditions = new JTable();
        stopConditions.setEnabled(false);
        JLabel      stopConditionsLabel;
        JSeparator  stopConditionsSeparator;
        JSeparator  titleSeparator;
        visualizations = new JTable(new String[0][0], new String[] {""});
        visualizations.setEnabled(false);
        JLabel      visualizationsLabel;
        JSeparator  visualizationsSeparator;

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        title = new JLabel();
        title.setFont(new Font("Arial", Font.PLAIN, 20));
        parametersLabel = new JLabel("Parameters");
        stopConditionsLabel = new JLabel("Stop Conditions");
        visualizationsLabel = new JLabel("Visualizations");
        fullDescription = new JTextPane();
        fullDescription.setEditable(false);
        fullDescription.setContentType("text/html");
        fullDescription.setBackground(frame.getBackground());
        fullDescription.setBorder(null);
        titleSeparator = new JSeparator();
        parametersSeparator = new JSeparator();
        stopConditionsSeparator = new JSeparator();
        visualizationsSeparator = new JSeparator();
        visualizations.setBackground(frame.getBackground());

        frame.getRootPane().registerKeyboardAction(new EscapeListener(), KeyStroke.getKeyStroke("ESCAPE"), 0);

        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(10,10,10)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(stopConditions.getTableHeader(), GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .add(stopConditions, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .add(titleSeparator, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .add(title)
                    .add(GroupLayout.TRAILING, fullDescription, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(parametersLabel)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(parametersSeparator, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(stopConditionsLabel)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(stopConditionsSeparator, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .add(parameters.getTableHeader(), GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .add(parameters, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(visualizationsLabel)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(visualizationsSeparator, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .add(layout.createParallelGroup(GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
//                    .add(22, 22, 22)
                    .add(visualizations, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .add(10,10,10)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(5,5,5)
                .add(title)
                .add(3,3,3)
                .add(titleSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(fullDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.TRAILING)
                    .add(parametersLabel)
                    .add(parametersSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(parameters.getTableHeader(), GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .add(parameters, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.TRAILING)
                    .add(stopConditionsLabel)
                    .add(stopConditionsSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(stopConditions.getTableHeader(), GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .add(stopConditions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.TRAILING)
                    .add(visualizationsLabel)
                    .add(visualizationsSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE))
                )
            .add(layout.createParallelGroup(GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
//                    .add(366, 366, 366)
                    .add(visualizations, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .add(10, 10, 10)))
        );
    }

    // private listeners
    private class EscapeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
        }
    }
}