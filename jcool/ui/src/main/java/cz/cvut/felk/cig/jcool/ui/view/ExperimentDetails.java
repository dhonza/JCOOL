/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.ui.model.PropertyTable;
import org.apache.commons.lang.SystemUtils;
import org.jdesktop.layout.GroupLayout;
import org.ytoh.configurations.AbstractProperty;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.util.PropertyExtractor;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author ytoh
 */
public class ExperimentDetails extends JPanel {

    // composition
    private Detail function;
    private Detail method;
    private Detail methodComponents;
    private Detail solver;
    private Detail conditions;
    private Detail visualizations;

    private cz.cvut.felk.cig.jcool.ui.model.ExperimentSetup experiment;
    private PropertyExtractor extractor;
    private cz.cvut.felk.cig.jcool.ui.model.ExperimentDetails model;

    public void setExperiment(cz.cvut.felk.cig.jcool.ui.model.ExperimentSetup experiment) {
        this.experiment = experiment;
    }

    public void setExtractor(PropertyExtractor extractor) {
        this.extractor = extractor;
    }

    public void setModel(cz.cvut.felk.cig.jcool.ui.model.ExperimentDetails model) {
        this.model = model;
    }

    public void initComponents() {
        function = new Detail("function", extractor, cz.cvut.felk.cig.jcool.ui.view.ExperimentDetails.Detail.Type.TABLE);
        experiment.addPropertyChangeListener(function);

        methodComponents = new DelegatedDetail("methodComponents", extractor, cz.cvut.felk.cig.jcool.ui.view.ExperimentDetails.Detail.Type.LIST, this.model);
        model.addPropertyChangeListener(methodComponents);

        method = new DelegatedDetail("method", extractor, cz.cvut.felk.cig.jcool.ui.view.ExperimentDetails.Detail.Type.TABLE, this.model);
        model.addPropertyChangeListener(method);

        solver = new Detail("solver", extractor, cz.cvut.felk.cig.jcool.ui.view.ExperimentDetails.Detail.Type.TABLE);
        experiment.addPropertyChangeListener(solver);

        conditions = new Detail("stopconditions", extractor, cz.cvut.felk.cig.jcool.ui.view.ExperimentDetails.Detail.Type.LIST);
        experiment.addPropertyChangeListener(conditions);

        visualizations = new Detail("visualizations", extractor, cz.cvut.felk.cig.jcool.ui.view.ExperimentDetails.Detail.Type.LIST);
        experiment.addPropertyChangeListener(visualizations);

        JTabbedPane pane = new JTabbedPane();
        pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        if(SystemUtils.IS_OS_MAC) {
            pane.setTabPlacement(JTabbedPane.RIGHT);
        }

        pane.addTab("Function", function);
        pane.addTab("Method", method);
        pane.addTab("MethodComponents", methodComponents);
        pane.addTab("Solver", solver);
        pane.addTab("Conditions", conditions);
        pane.addTab("Visualizations", visualizations);

        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
    }

    /**
     * According to TYPE renders one or more PropertyTables to its own tab(panel).
     */
    static class Detail extends JPanel implements PropertyChangeListener {

        static enum Type { LIST, TABLE }

        protected String key;
        protected PropertyExtractor extractor;
        protected Type type;
        protected List<PropertyTable> propertyTables = new ArrayList<PropertyTable>();

        private Detail(String key, PropertyExtractor extractor, Type type) {
            this.key = key;
            this.extractor = extractor;
            this.type = type;
            initComponents();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() == evt.getOldValue()){
                return;
            }
            if (evt.getPropertyName().equals(key) && evt.getNewValue() != null ) {
                removeAll();
                propertyTables = new ArrayList<PropertyTable>();
                switch (type) {
                    case LIST:
                        List<?> l = (List<?>) evt.getNewValue();

                        final JPanel panel = new JPanel();
                        panel.setBorder(BorderFactory.createEmptyBorder());
                        GroupLayout layout = new GroupLayout(panel);
                        panel.setLayout(layout);
                        GroupLayout.ParallelGroup horizontal = layout.createParallelGroup();
                        GroupLayout.SequentialGroup vertical = layout.createSequentialGroup();
                        for (int i = 0, size = l.size(); i < size; i++) {
                            PropertyTable table = new PropertyTable(l.get(i), extractor);
                            propertyTables.add(table);
                            JTableHeader tableHeader = table.getTableHeader();
                            horizontal.add(layout.createParallelGroup(GroupLayout.LEADING).add(tableHeader).add(table));
                            vertical.add(layout.createSequentialGroup().add(tableHeader).add(table));
                        }
                        layout.setHorizontalGroup(horizontal);
                        layout.setVerticalGroup(vertical);
                        add(new JScrollPane(panel), BorderLayout.CENTER);
                        break;
                    case TABLE:
                        PropertyTable table = new PropertyTable(evt.getNewValue(), extractor);
                        propertyTables.add(table);
                        add(new JScrollPane(table), BorderLayout.CENTER);
                        break;
                }
                
                repaint();
                getParent().validate();
            }
        }

        private void initComponents() {
            setLayout(new BorderLayout());
        }
    }

    /**
     * Registers delegate as a PropertyChangeListener for all given component(s).
     */
    static class DelegatedDetail extends Detail {

        PropertyChangeListener delegate;

        private DelegatedDetail(String key, PropertyExtractor extractor, Type type, PropertyChangeListener delegate) {
            super(key, extractor, type);
            this.delegate = delegate;
        }

        /**
         * Adds delegate as listener for PropertyChangeEvent for extracted component(s).
         * @param evt - propertyChangeEvent to process.
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() == evt.getOldValue()){
                return;
            }

            if (evt.getPropertyName().equals(key) && evt.getNewValue() != null) {
                // create PropertyTable
                super.propertyChange(evt);
                // register the delegate as PropertyChangeListener
                for (PropertyTable table : this.propertyTables){
                    for (Property property : table.getProperties()){
                        if (property instanceof AbstractProperty){
                            // unregister the listener in case the property is the same instance
                            ((AbstractProperty)property).removePropertyChangeListener(this.delegate);
                            ((AbstractProperty)property).addPropertyChangeListener(this.delegate);
                        }
                    }
                }
            }
            repaint();
            getParent().validate();
        }
    }
}
