/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.Producer;
import cz.cvut.felk.cig.jcool.core.ValueTelemetry;
import cz.cvut.felk.cig.jcool.experiment.Iteration;
import cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

/**
 *
 * @author ytoh
 */
public class IterationVisualization implements TelemetryVisualization<ValueTelemetry> {
    private long startTime;
    private int iteration;
    private double best = Double.MAX_VALUE;
    private double current;
    // LABELS
    private JLabel elapsedTime;
    private JLabel iterations;
    private JLabel currentValue;
    private JLabel bestValue;
    // CONTAINER PANEL
    private JPanel panel;

    public void attachTo(JPanel panel) {
        this.panel = panel;
        panel.removeAll();
        initComponents();
    }

    public void init(Function function) {
        best = Double.MAX_VALUE;
    }
    
    private void initComponents() {
        elapsedTime  = new JLabel("0:00:00");
        iterations   = new JLabel("0");
        currentValue = new JLabel("0.0");
        bestValue    = new JLabel("0.0");

        startTime = System.currentTimeMillis();

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

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

    public Class<ValueTelemetry> getAcceptableType() {
        return ValueTelemetry.class;
    }

    public void notifyOf(Producer<? extends Iteration<ValueTelemetry>> producer) {
        Iteration<ValueTelemetry> i = producer.getValue();

        if(i != null && i.getValue() != null) {
            long duration = (System.currentTimeMillis() - this.startTime) / 1000;
            int seconds = (int)duration % 60;
            duration /= 60;
            int minutes = (int)duration % 60;
            duration /= 60;
            int hours = (int)duration %60;
            this.iteration = i.getIteration();
            this.current = i.getValue().getValue();
            if(current < best) {
                best = current;
            }

            elapsedTime.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
            iterations.setText(String.valueOf(iteration));
            currentValue.setText(String.format("%+10.5E", current));
            bestValue.setText(String.format("%+10.5E", best));

            panel.repaint();
        }
    }
}
