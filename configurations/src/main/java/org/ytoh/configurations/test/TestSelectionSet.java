package org.ytoh.configurations.test;

import org.ytoh.configurations.module.BasicModule;
import org.ytoh.configurations.module.Module;
import org.ytoh.configurations.module.Modules;
import org.ytoh.configurations.module.XmlSerializer;
import org.ytoh.configurations.ui.SelectionSetModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Oct 10, 2009
 * Time: 10:46:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSelectionSet implements PropertyChangeListener {

    String [] set;
    SelectionSetModel<String> selset;

    public TestSelectionSet() {
        set = new String[]{"jedna", "dve", "tri", "ctyri", "pet"};
        selset = new SelectionSetModel<String>(set);

        selset.addPropertyChangeListener(this);
    }


    public void propertyChange(PropertyChangeEvent evt) {
        System.out.printf("Something changed :)\n");
        System.out.printf("param %s\n", evt.getPropagationId());
    }

    private void printArray(String [] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.printf(" * item %d = %s\n",i,arr[i]);
        }
    }

    public void test() {
        String [] s;
        s = selset.getEnabledElements(String.class);
        System.out.printf("Enabled elements:\n");
        printArray(s);
        s = selset.getDisabledElements(String.class);
        System.out.printf("Disabled elements:\n");
        printArray(s);

        selset.disableElement(1);
        selset.disableElement(3);

        s = selset.getEnabledElements(String.class);
        System.out.printf("Enabled elements:\n");
        printArray(s);
        s = selset.getDisabledElements(String.class);
        System.out.printf("Disabled elements:\n");
        printArray(s);
    }

    public void testSerializer() {
        XmlSerializer ser = new XmlSerializer();
        Bean4 b = Bean4.getInstance();
        b.setModel(selset);

        Module m = BasicModule.withName("Name", Modules.getRootModule()).withComponent(b).build();
        ser.setConfigurationDirectory("./testconf");
        ser.serializeModule(m);
    }

    public static void main(String []args) {

        TestSelectionSet tst = new TestSelectionSet();

        tst.test();
        tst.testSerializer();
    }

}
