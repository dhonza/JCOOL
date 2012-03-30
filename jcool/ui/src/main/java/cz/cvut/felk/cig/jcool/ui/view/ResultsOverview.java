/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.experiment.ExperimentRun;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 *
 * @author ytoh
 */
public class ResultsOverview extends JPanel {

    private ExperimentRun experimentRun;

    public ResultsOverview(ExperimentRun experimentRun) {
        this.experimentRun = experimentRun;
        initComponents();
    }

    private void initComponents() {
        JPanel results = new javax.swing.JPanel();
        JLabel valueEvalsLabel = new javax.swing.JLabel();
        JLabel gradientEvalsLabel = new javax.swing.JLabel();
        JLabel hessianEvalsLabel = new javax.swing.JLabel();
        JLabel valueEvals = new javax.swing.JLabel();
        JLabel gradientEvals = new javax.swing.JLabel();
        JLabel hessianEvals = new javax.swing.JLabel();
        JLabel solutionLabel = new javax.swing.JLabel();
        JLabel solution = new javax.swing.JLabel();
        JLabel stopConditionsLabel = new javax.swing.JLabel();
        JLabel stopConditions = new javax.swing.JLabel();
        JPanel overview = new javax.swing.JPanel();
        JLabel functionLabel = new javax.swing.JLabel();
        JLabel methodLabel = new javax.swing.JLabel();
        JLabel solverLabel = new javax.swing.JLabel();
        JLabel method = new javax.swing.JLabel();
        JLabel solver = new javax.swing.JLabel();
        JLabel function = new javax.swing.JLabel();

        overview.setBorder(BorderFactory.createEmptyBorder());

        results.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));

        valueEvalsLabel.setText("Value evaluations");

        gradientEvalsLabel.setText("Gradient evaluations");

        hessianEvalsLabel.setText("Hessian evaluations");

        valueEvals.setText(String.valueOf(experimentRun.getResults().getStatistics().getValueAt()));

        gradientEvals.setText(String.valueOf(experimentRun.getResults().getStatistics().getGradientAt()));

        hessianEvals.setText(String.valueOf(experimentRun.getResults().getStatistics().getHessianAt()));

        solutionLabel.setText("Solution");

        solution.setText(String.valueOf(experimentRun.getResults().getSolution()));

        stopConditionsLabel.setText("Stopped on");

        stopConditions.setText(String.valueOf(experimentRun.getResults().getMetConditions()));

        GroupLayout resultsLayout = new GroupLayout(results);
        results.setLayout(resultsLayout);
        resultsLayout.setHorizontalGroup(
            resultsLayout.createParallelGroup(GroupLayout.LEADING)
            .add(resultsLayout.createSequentialGroup()
                .addContainerGap()
                .add(resultsLayout.createParallelGroup(GroupLayout.LEADING)
                    .add(resultsLayout.createSequentialGroup()
                        .add(solutionLabel)
                        .addPreferredGap(LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(solution))
                    .add(resultsLayout.createSequentialGroup()
                        .add(stopConditionsLabel)
                        .addPreferredGap(LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(stopConditions))
                    .add(resultsLayout.createSequentialGroup()
                        .add(valueEvalsLabel)
                        .addPreferredGap(LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(valueEvals))
                    .add(resultsLayout.createSequentialGroup()
                        .add(gradientEvalsLabel)
                        .addPreferredGap(LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(gradientEvals))
                    .add(resultsLayout.createSequentialGroup()
                        .add(hessianEvalsLabel)
                        .addPreferredGap(LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(hessianEvals)))
                .addContainerGap())
        );
        resultsLayout.setVerticalGroup(
            resultsLayout.createParallelGroup(GroupLayout.LEADING)
            .add(resultsLayout.createSequentialGroup()
                .addContainerGap()
                .add(resultsLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(solutionLabel)
                    .add(solution))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(resultsLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(stopConditionsLabel)
                    .add(stopConditions))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(resultsLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(valueEvalsLabel)
                    .add(valueEvals))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(resultsLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(gradientEvalsLabel)
                    .add(gradientEvals))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(resultsLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(hessianEvalsLabel)
                    .add(hessianEvals))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        functionLabel.setText("Function");

        methodLabel.setText("Optimization method");

        solverLabel.setText("Solver");

        method.setText(String.valueOf(experimentRun.getMethod().getName()));

        solver.setText(String.valueOf(experimentRun.getSolver().getName()));

        function.setText(String.valueOf(experimentRun.getFunction().getName()));

        GroupLayout overviewLayout = new GroupLayout(overview);
        overview.setLayout(overviewLayout);
        overviewLayout.setHorizontalGroup(
            overviewLayout.createParallelGroup(GroupLayout.LEADING)
            .add(overviewLayout.createSequentialGroup()
                .addContainerGap()
                .add(overviewLayout.createParallelGroup(GroupLayout.LEADING)
                    .add(overviewLayout.createSequentialGroup()
                        .add(methodLabel)
                        .addPreferredGap(LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(method))
                    .add(overviewLayout.createSequentialGroup()
                        .add(functionLabel)
                        .addPreferredGap(LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(function))
                    .add(overviewLayout.createSequentialGroup()
                        .add(solverLabel)
                        .addPreferredGap(LayoutStyle.RELATED, 50, Short.MAX_VALUE)
                        .add(solver)))
                .addContainerGap())
        );
        overviewLayout.setVerticalGroup(
            overviewLayout.createParallelGroup(GroupLayout.LEADING)
            .add(overviewLayout.createSequentialGroup()
                .addContainerGap()
                .add(overviewLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(functionLabel)
                    .add(function))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(overviewLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(methodLabel)
                    .add(method))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(overviewLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(solverLabel)
                    .add(solver))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.TRAILING)
                    .add(results, GroupLayout.LEADING, GroupLayout.DEFAULT_SIZE, 800)
                    .add(overview, GroupLayout.LEADING, GroupLayout.DEFAULT_SIZE, 800))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(overview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//                .addPreferredGap(LayoutStyle.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(results, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }
}
