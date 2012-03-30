/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.view;

import java.awt.Component;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;
import org.jdesktop.layout.LayoutStyle;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.util.PropertyExtractor;

/**
 *
 * @author ytoh
 */
public class ExperimentSetup extends JPanel {
    private cz.cvut.felk.cig.jcool.ui.model.ExperimentSetup experiment;
    private PropertyExtractor extractor;

    public void setExperiment(cz.cvut.felk.cig.jcool.ui.model.ExperimentSetup experiment) {
        this.experiment = experiment;
    }

    public void setExtractor(PropertyExtractor extractor) {
        this.extractor = extractor;
    }

    public void initComponents() {
        List<Property> properties = extractor.propertiesFor(experiment);
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        SequentialGroup horizontal = layout.createSequentialGroup();
        SequentialGroup vertical = layout.createSequentialGroup();

        vertical.addContainerGap();
        horizontal.addContainerGap();

        ParallelGroup parallel = layout.createParallelGroup(GroupLayout.LEADING);

        for (Property property : properties) {
            JLabel label = new JLabel(property.getName());
            Component editor = property.getEditorComponent();

            parallel.add(layout.createSequentialGroup().add(label).addPreferredGap(LayoutStyle.RELATED).add(editor).addContainerGap());
            vertical.add(layout.createSequentialGroup().add(layout.createParallelGroup(GroupLayout.BASELINE).add(label).add(editor)).addPreferredGap(LayoutStyle.RELATED));
        }

        horizontal.add(parallel);

        vertical.addContainerGap(5, Short.MAX_VALUE);
        horizontal.addContainerGap();

        layout.setHorizontalGroup(horizontal);
        layout.setVerticalGroup(vertical);
    }
}
