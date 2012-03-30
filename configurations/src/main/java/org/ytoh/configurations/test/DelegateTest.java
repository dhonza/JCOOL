package org.ytoh.configurations.test;

import org.ytoh.configurations.module.*;
import org.ytoh.configurations.ui.SelectionSetDelegate;
import org.ytoh.configurations.ui.SelectionSetModel;

import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Nov 17, 2009
 * Time: 8:58:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DelegateTest {

    static SelectionSetDelegate deleg;

    public static void main(String [] args) throws FileNotFoundException {

//        deleg = new SelectionSetDelegate();
//        File outputFile = new File("./testconf", "pokus.xml");
//        XMLEncoder encoder = new XMLEncoder(System.out);//new FileOutputStream(outputFile));
//        encoder.setPersistenceDelegate(SelectionSetModel.class, deleg);
//
//        encoder.setExceptionListener(new ExceptionListener() {
//            public void exceptionThrown(Exception e) {
//                System.out.printf("Exception %s\n",e.getMessage());
//                System.out.flush();
//                e.printStackTrace();
//                System.err.flush();
//            }
//        });
//
//
//        SelectionSetModel b = new SelectionSetModel<String>(new String [] {"a", "b"});
//        encoder.writeObject(b);
//        encoder.flush();
//        encoder.close();

        SelectionSetModel b = new SelectionSetModel<String>(new String [] {"a", "b", "c", "d"});
        b.disableAllElements(); 
        b.enableElement(1);
        b.enableElement(3);
        Module m = BasicModule.withName("xxx", Modules.getRootModule()).withComponent(b).build();

        deleg = new SelectionSetDelegate();
        XmlSerializer ser = new XmlSerializer();
        ser.addNewDelegate(SelectionSetModel.class, deleg);

        ser.setConfigurationDirectory("./testconf");
        ser.serializeModule(Modules.getRootModule());

        b.disableAllElements();


        int [] enabledIndices;

        enabledIndices = b.getEnableElementIndices();
        System.out.printf("Enabled elements:\n");
        for (int i = 0; i < enabledIndices.length; i++) {
            System.out.printf("%d ", enabledIndices[i]);
        }
        System.out.printf("\n");

        XmlDeserializer deser = new XmlDeserializer();
        deser.setConfigurationDirectory("./testconf");
        deser.deserializeModule(Modules.getRootModule());

        m = Modules.getModule("/xxx");
        b = (SelectionSetModel) m.getComponents().toArray()[0];

        enabledIndices = b.getEnableElementIndices();
        System.out.printf("Enabled elements:\n");
        for (int i = 0; i < enabledIndices.length; i++) {
            System.out.printf("%d ", enabledIndices[i]);
        }
        System.out.printf("\n");

    }

}
