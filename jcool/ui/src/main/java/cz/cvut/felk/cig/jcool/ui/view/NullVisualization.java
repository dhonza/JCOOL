/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.experiment.Iteration;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.Producer;
import cz.cvut.felk.cig.jcool.core.Telemetry;
import javax.swing.JPanel;
import org.ytoh.configurations.annotations.Component;

/**
 *
 * @author ytoh
 */
@Component(name="No visualization")
public class NullVisualization implements cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization<Telemetry> {

    public void init(Function function) {
    }

    public void attachTo(JPanel panel) {
    }

    public Class<Telemetry> getAcceptableType() {
        return Telemetry.class;
    }

    public void notifyOf(Producer<? extends Iteration<Telemetry>> producer) {
    }
}
