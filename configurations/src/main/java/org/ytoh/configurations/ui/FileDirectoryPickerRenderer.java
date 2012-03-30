package org.ytoh.configurations.ui;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.annotations.FileDirectoryPicker;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Oct 7, 2009
 * Time: 5:08:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileDirectoryPickerRenderer extends JPanel implements PropertyRenderer<String, FileDirectoryPicker>, PropertyChangeListener {
    JLabel text;
    Property<String> prop;

    public FileDirectoryPickerRenderer() {
        super();

        text = new JLabel();
        setLayout(new FormLayout("fill:50px:grow","fill:20px"));
        CellConstraints cc = new CellConstraints();
        add(text, cc.xy(1,1));

    }

    public Component getRendererComponent(Property<String> property, FileDirectoryPicker annotation) {

        prop = property;

        ValueModel model = new PropertyAdapter(property, "value", true);
        model.addValueChangeListener(this);

        text.setText(annotation.value());

        return this;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        text.setText(prop.getValue());
    }
}
