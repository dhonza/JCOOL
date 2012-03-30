/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.ui.model.Visualization;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.util.PropertyExtractor;

/**
 *
 * @author ytoh
 */
public class VisualizationControls extends JPanel {
    private cz.cvut.felk.cig.jcool.ui.model.Visualization visualization;
    private PropertyExtractor extractor;

    public void setExtractor(PropertyExtractor extractor) {
        this.extractor = extractor;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }

    /**
     *
     */
    public void initComponents() {
        List<Property> properties = extractor.propertiesFor(visualization);
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT));

        for (Property property : properties) {
            add(new JLabel(property.getName()));
            add(property.getEditorComponent());
        }

        repaint();
        if(getParent() != null) {
            getParent().validate();
        }
    }
}
