/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui;

import cz.cvut.felk.cig.jcool.benchmark.function.RosenbrockFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.direct.DirectMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.cg.ConjugateGradientMethod;
import cz.cvut.felk.cig.jcool.core.Telemetry;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRun;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRunSaver;
import cz.cvut.felk.cig.jcool.solver.SolverFactory;
import cz.cvut.felk.cig.jcool.ui.util.ViewUtils;
import cz.cvut.felk.cig.jcool.experiment.BasicExperimentRunner;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRunner;
import cz.cvut.felk.cig.jcool.ui.util.JTextAreaAppender;
import cz.cvut.felk.cig.jcool.ui.view.Experiment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ytoh
 */
public class Boot {

    static final Logger logger = Logger.getLogger(Boot.class);

    static Configuration configuration;
    static {
        try {
            configuration = new PropertiesConfiguration("jcool.properties");
        } catch (ConfigurationException ex) {
            logger.fatal("Could not load configuration file: jcool.properties", ex);
        }
    }

    /**
     * 
     * @param args
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws javax.swing.UnsupportedLookAndFeelException
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, ConfigurationException, FileNotFoundException {
        Options options = getOptions();
        
        try {
            // parse the input
            Parser parser = new PosixParser();
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                // display help and usage
                HelpFormatter formatter = new HelpFormatter();

                String[] header = configuration.getStringArray("help");
                StringBuilder message = new StringBuilder(header[0]);
                for (int i = 1; i < header.length; i++) {
                    message.append(" ").append(header[i]);
                }

                formatter.printHelp("jcool", message.toString(), options, configuration.getString("help.footer"), true);
                System.exit(0);
            }

            if (line.hasOption("headless")) {
                // start headless mode
                headlessMode(line);
            } else {
                // if no paramters were passed start GUI
                guiMode(line);
            }
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("jcool", options, true);
            System.exit(1);
        }
    }

    /**
     * 
     * @return
     */
    private static Options getOptions() {
        Options options = new Options();
        Option help = new Option("h", "help", false, "Print help message.");
        @SuppressWarnings("static-access")
        Option headless = OptionBuilder.withLongOpt("headless").hasArgs().withArgName("experiment definition").withDescription("Start in headless mode. Execute experiments according experiment definitions.").create();
        options.addOption(help);
        options.addOption(headless);
        return options;
    }

    /**
     * 
     * @param line
     */
    public static void headlessMode(CommandLine line) throws FileNotFoundException {
        logger.info("starting headless mode");
        final ExecutorService es = Executors.newSingleThreadExecutor();

        final ExperimentRunner runner = new BasicExperimentRunner(es);

        runner.setFunction(new RosenbrockFunction());
        runner.setMethod(new DirectMethod());
        runner.setSolver(SolverFactory.getNewInstance(1000));
        
        runner.startExperiment();

        ExperimentRun run = runner.getExperimentResults();
        ExperimentRunSaver saver = new ExperimentRunSaver(run);

        // present the results
        logger.info("====================");
        logger.info("Function: " + run.getFunction().getName());
        logger.info("Method: " + run.getMethod().getName());
        logger.info("Solver: " + run.getSolver().getName());
        logger.info("--------------------");
        logger.info("Solution: " + run.getResults().getSolution());
        logger.info("# of iterations: " + run.getResults().getNumberOfIterations());
        logger.info("Statistics: " + run.getResults().getStatistics());
        logger.info("--------------------");

        int i = 1;
        for (List<? extends Telemetry> list : run.getProgress()) {
            for (Telemetry telemetry : list) {
                logger.debug(i++ + ": " + telemetry.getValue());
            }
        }
        logger.info("====================");
        // /present the results

        saver.save();

        logger.info("Experiment results saved to file: " + saver.getDefaultFilename());
        es.shutdown();
    }

    /**
     *
     * @param line
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws javax.swing.UnsupportedLookAndFeelException
     */
    public static void guiMode(CommandLine line) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        logger.info("starting GUI mode");
        // set the default platform L&F
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // create the application context
        final ApplicationContext context = new ClassPathXmlApplicationContext("spring-application.xml");
        // tell log4j to append to the log text area
        JTextAreaAppender.setTextarea((JTextArea) context.getBean("view.logTextArea"));

        logger.info("JCool started");

        // get the GUI
        final Experiment experiment = (Experiment) context.getBean("view.experiment");

        experiment.setTitle(configuration.getString("application.title"));

        // set exit on close
        experiment.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                logger.info("JCool stopped");
                System.exit(0);
            }
        });

        ViewUtils.fullScreen(experiment);

        // start the GUI
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                logger.info("Experiment window opened");
                experiment.setVisible(true);
                // workaroung for propper JLayeredPane diplay
                // @see view.Visualization
                ((JPanel) context.getBean("view.visualization")).repaint();
            }
        });
    }
}
