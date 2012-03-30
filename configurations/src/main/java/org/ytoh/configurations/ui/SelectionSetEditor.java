package org.ytoh.configurations.ui;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.ytoh.configurations.Property;
import org.ytoh.configurations.annotations.SelectionSet;
import org.ytoh.configurations.context.Publisher;
import org.ytoh.configurations.context.PublishingContext;
import org.ytoh.configurations.context.Subscriber;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Oct 11, 2009
 * Time: 12:25:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class SelectionSetEditor extends JDialog implements PropertyEditor<SelectionSetModel, SelectionSet>, ActionListener, PropertyChangeListener, ListSelectionListener {

    private JList list;
    private DefaultListModel listModel;
    JPanel mainPanel;
    JButton dialogCloseButton;
    JButton editorButton;

    private JScrollPane scrollPane;
    JList availableMethods;

    private Subscriber subscriber;
    private SelectionSet annotation;
    private Property<SelectionSetModel> property;

    private boolean visible;
    FormLayout layout;

    public SelectionSetEditor() {
        super();

        availableMethods = new JList();
        listModel = new DefaultListModel();

        //String [] s = {"a","b", "c"};

        list = new JList();
        list.setModel(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);

        list.addListSelectionListener(this);

        dialogCloseButton = new JButton("Save selection and close");
        dialogCloseButton.addActionListener(this);


        scrollPane = new JScrollPane(list);
//        scrollPane.add(list);

        layout = new FormLayout("fill:200px:grow", "fill:200px:grow,30px");
        mainPanel = new JPanel(layout);

        CellConstraints cc = new CellConstraints();
        mainPanel.add(scrollPane, cc.xy(1, 1));
        mainPanel.add(dialogCloseButton, cc.xy(1, 2));
        setContentPane(mainPanel);

        setMinimumSize(new Dimension(300, 500));
        setSize(new Dimension(300, 500));
        visible = false;
        pack();
    }

    public Component getEditorComponent(Property<SelectionSetModel> selectionSetModelProperty, SelectionSet annotation, PublishingContext context) {

        this.annotation = annotation;
        property = selectionSetModelProperty;
        ValueModel model = new PropertyAdapter(property, "value", true);
        model.addValueChangeListener(this);

        Class<?> type = annotation.type();
        String key = annotation.key();

        subscriber = new SelectionSetEditorSubscriber();
        context.subscribeTo(type, key, subscriber);
        subscriber.notifyOf(context, context.getList(type, key), key);

        setContentPane(mainPanel);
        setTitle(annotation.windowTitle());

        editorButton = new JButton("Close Selection Window");
        editorButton.addActionListener(this);
        return editorButton;
    }

    public void actionPerformed(ActionEvent e) {
        if (isVisible()) {
            editorButton.setText("Open selection");
            setVisible(false);
            setModal(false);
        } else {
            editorButton.setText("Close selection");
            setModal(true);
            setVisible(true);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
//        System.out.printf("Editor property = %s\n",property.getValue());
        SelectionSetModel selSet = property.getValue();
//        selSet.enableAllElements();
        Object[] o = selSet.getAllElements();
        listModel.clear();
        for (int i = 0; i < o.length; i++) {
//            System.out.printf("%d - %s\n",i,o[i].toString());
            listModel.addElement(o[i].toString());
        }

        list.setModel(listModel);
        int [] selIdcs = selSet.getEnableElementIndices();
/*        for (int i = 0; i < selIdcs.length; i++) {
            list.setS;
        }
        list.addSelectionInterval(0, o.length - 1);*/
        list.setSelectedIndices(selIdcs);
        list.revalidate();
        repaint();

    }

    public void valueChanged(ListSelectionEvent e) {
        SelectionSetModel selSet = property.getValue();
        selSet.disableAllElements();
//        System.out.printf("start %d end %d\n",e.getFirstIndex(), e.getLastIndex());

        int[] indices = list.getSelectedIndices();
//        System.out.printf("length %d\n",indices.length);
//        System.out.printf("%s\n",e.toString());

        for (int i = 0; i < indices.length; i++) {
//            System.out.printf("%d\n",indices[i]);
            selSet.enableElement(indices[i]);
        }


    }

    private class SelectionSetEditorSubscriber implements Subscriber {

        public void notifyOf(Publisher publisher, java.util.List value, String key) {
//            System.out.printf("Editor property = %s\n",property.getValue());
        }

    }


}
