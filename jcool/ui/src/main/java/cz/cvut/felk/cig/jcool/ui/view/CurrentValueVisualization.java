/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.Producer;
import cz.cvut.felk.cig.jcool.core.ValueTelemetry;
import cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization;
import cz.cvut.felk.cig.jcool.experiment.Iteration;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.ytoh.configurations.annotations.Component;

/**
 *
 * @author ytoh
 */
@Component(name="Show current best")
public class CurrentValueVisualization implements TelemetryVisualization<ValueTelemetry> {

    private JPanel panel;
    private DefaultCategoryDataset dataSet;
    private double current = Double.MAX_VALUE;

    public void init(Function function) {
    }

    public void attachTo(JPanel panel) {
        this.panel = panel;
        dataSet = new DefaultCategoryDataset();

        panel.removeAll();
        initComponents();
    }

    private void initComponents() {
        panel.setLayout(new BorderLayout());

        ChartPanel chart = new ChartPanel(ChartFactory.createLineChart(
                "Current value", // chart title
                "iterations", // domain axis label
                "Value", // range axis label
                dataSet, // data
                PlotOrientation.VERTICAL, // orientation
                false, // include legend
                true, // tooltips
                false // urls
                ));

        chart.setDomainZoomable(true);

        panel.add(chart, BorderLayout.CENTER);
    }

    public Class<ValueTelemetry> getAcceptableType() {
        return ValueTelemetry.class;
    }

    public void notifyOf(Producer<? extends Iteration<ValueTelemetry>> producer) {
        Iteration<ValueTelemetry> i = producer.getValue();

        if (i != null && i.getValue() != null) {
            int iteration = i.getIteration();
            current = i.getValue().getValue();

            dataSet.addValue((Number) current, "Current value", iteration);
        }
    }
}
