/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.experiment;

import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.solver.OptimizationResults;
import cz.cvut.felk.cig.jcool.solver.Statistics;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.ytoh.configurations.util.ComponentInfo;

/**
 *
 * @author ytoh
 */
public class ExperimentRunSaver {

    /**
     * 
     */
    private static final PersistenceDelegate POINT_PD = new PersistenceDelegate() {

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Point point = (Point) oldInstance;
            return new Expression(oldInstance, Point.class, "at", new Object[]{point.toArray()});
        }
    };
    /**
     *
     */
    private static final PersistenceDelegate VALUE_POINT_PD = new PersistenceDelegate() {

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            ValuePoint point = (ValuePoint) oldInstance;
            return new Expression(oldInstance, ValuePoint.class, "at", new Object[]{point.getPoint(), point.getValue()});
        }
    };
    /**
     *
     */
    private static final PersistenceDelegate VALUE_POINT_NEW_PD = new PersistenceDelegate() {

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            ValuePointColored point = (ValuePointColored) oldInstance;
            return new Expression(oldInstance, ValuePointColored.class, "at", new Object[]{point.getPoint(), point.getValue(), point.getBest()});
        }
    };
    /**
     *
     */
    private static final PersistenceDelegate TELEMETRY_PD = new DefaultPersistenceDelegate(new String[]{"value"});
    /**
     *
     */
    private static final PersistenceDelegate EXPERIMENT_RUN_PD = new DefaultPersistenceDelegate(new String[]{"function", "solver", "method", "stopConditions", "results", "progress"});
    /**
     *
     */
    private static final PersistenceDelegate COMPONENT_INFO_PD = new DefaultPersistenceDelegate(new String[]{"name", "properties"});
    /**
     *
     */
    private static final PersistenceDelegate OPTIMIZATION_RESULT_PD = new DefaultPersistenceDelegate(new String[]{"solution", "statistics", "numberOfIterations", "metConditions"});
    /**
     *
     */
    private static final PersistenceDelegate STATISTICS_PD = new DefaultPersistenceDelegate(new String[]{"valueAt", "gradientAt", "hessianAt"});

    //
    private final ExperimentRun run;
    //
    private final String defaultFilename;

    /**
     *
     * @param run
     */
    public ExperimentRunSaver(ExperimentRun run) {
        this.run = run;
        this.defaultFilename = String.format("[%s]%s-%s.jcr", new SimpleDateFormat("HH-mm").format(new Date()), run.getFunction().getName(), run.getMethod().getName());
    }

    /**
     * 
     * @return
     */
    public String getDefaultFilename() {
        return defaultFilename;
    }

    /**
     *
     */
    public void save() throws FileNotFoundException {
        save(defaultFilename);
    }

    /**
     *
     * @param filename
     */
    public void save(String filename) throws FileNotFoundException {
       save(new File(filename));
    }

    /**
     *
     * @param filename
     */
    public void save(File file) throws FileNotFoundException {
        XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));
        encoder.setPersistenceDelegate(Point.class, POINT_PD);
        encoder.setPersistenceDelegate(ValuePoint.class, VALUE_POINT_PD);
        encoder.setPersistenceDelegate(ValuePointColored.class, VALUE_POINT_NEW_PD);
        encoder.setPersistenceDelegate(ValueTelemetry.class, TELEMETRY_PD);
        encoder.setPersistenceDelegate(ValuePointTelemetry.class, TELEMETRY_PD);
        encoder.setPersistenceDelegate(ValuePointListTelemetry.class, TELEMETRY_PD);
        encoder.setPersistenceDelegate(ValuePointListTelemetryColored.class, TELEMETRY_PD);
        encoder.setPersistenceDelegate(ExperimentRun.class, EXPERIMENT_RUN_PD);
        encoder.setPersistenceDelegate(ComponentInfo.class, COMPONENT_INFO_PD);
        encoder.setPersistenceDelegate(OptimizationResults.class, OPTIMIZATION_RESULT_PD);
        encoder.setPersistenceDelegate(Statistics.class, STATISTICS_PD);

        encoder.writeObject(run);
        encoder.flush();
        encoder.close();
    }
}
