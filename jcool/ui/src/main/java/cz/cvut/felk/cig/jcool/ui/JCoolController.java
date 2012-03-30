/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui;

import cz.cvut.felk.cig.jcool.ui.model.FunctionDetail;
import cz.cvut.felk.cig.jcool.ui.model.OptimizationMethodDetail;
import cz.cvut.felk.cig.jcool.ui.util.ViewUtils;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author ytoh
 */
public class JCoolController {
    private OptimizationMethodView methodDetailView;
    private FunctionView functionDetailView;
    private JExpandList functionList;
    private JExpandList methodList;

    public void displayMethodDetail(int row) {
        final OptimizationMethodDetail detail = ((OptimizationMethodDetail)methodList.getValueAt(row, 0));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = methodDetailView.getFrame(detail);
                ViewUtils.toTopRightCorner(frame);
                frame.setVisible(true);
            }
        });
    }

    public void displayFunctionDetail(int row) {
        final FunctionDetail detail = ((FunctionDetail)functionList.getValueAt(row, 0));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                functionDetailView.getFrame(detail).setVisible(true);
            }
        });
    }

    public void setFunctionList(JExpandList functionList) {
        this.functionList = functionList;
    }

    public void setMethodList(JExpandList methodList) {
        this.methodList = methodList;
    }

    public void setFunctionDetailView(FunctionView functionDetailView) {
        this.functionDetailView = functionDetailView;
    }

    public void setMethodDetailView(OptimizationMethodView methodDetailView) {
        this.methodDetailView = methodDetailView;
    }
}