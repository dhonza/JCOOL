/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

/**
 *
 * @author ytoh
 */
public class Telemetry extends JPanel {
    private JLabel elapsedTime;
    private JLabel iterations;
    private JLabel currentValue;
    private JLabel bestValue;

    public void initComponents() {
        elapsedTime = new JLabel("0:00:00");
        iterations = new JLabel("0");
        currentValue = new JLabel("0.0");
        bestValue = new JLabel("0.0");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        JLabel elapsedTimeLabel  = new JLabel("Elapsed time:");
        JLabel iterationsLabel   = new JLabel("Iteration:");
        JLabel currentValueLabel = new JLabel("Current value:");
        JLabel bestValueLabel    = new JLabel("Best value:");

        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(elapsedTimeLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(elapsedTime))
                    .add(layout.createSequentialGroup()
                        .add(iterationsLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(iterations))
                    .add(layout.createSequentialGroup()
                        .add(currentValueLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(currentValue))
                    .add(layout.createSequentialGroup()
                        .add(bestValueLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(bestValue)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(elapsedTimeLabel)
                    .add(elapsedTime))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(iterationsLabel)
                    .add(iterations))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(currentValueLabel)
                    .add(currentValue))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bestValueLabel)
                    .add(bestValue))
                .addContainerGap(5, Short.MAX_VALUE))
        );
    }
}
