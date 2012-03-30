package org.ytoh.configurations.test;

import org.ytoh.configurations.ConfigurationException;
import org.ytoh.configurations.context.DefaultContext;
import org.ytoh.configurations.context.DefaultPublishingContext;
import org.ytoh.configurations.ui.PropertyTable;
import org.ytoh.configurations.ui.SelectionSetModel;
import org.ytoh.configurations.util.AnnotationPropertyExtractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author ytoh
 */
public class Boot3 {

    static Bean4 b;

    public static void main(String[] args) throws ConfigurationException {

//        Module core = BasicModule.withName("core", Modules.getRootModule())
//                .withComponent(new Object())
//                .build();
//
//        Module moduleA = BasicModule.withName("moduleA", core)
//                .withComponent(new Object())
//                .build();
//
//        Module moduleB = BasicModule.withName("moduleB", core)
//                .withComponent(new Object())
//                .build();
//
//        Module extension = BasicModule.withName("extension", Modules.getRootModule())
//                .withComponent(new Object())
//                .build();
//
//        Module module1 = BasicModule.withName("module1", extension)
//                .withComponent(new Bean2())
//                .build();
//
//        System.out.println(Modules.getModuleFullName(Modules.getRootModule()));
//        System.out.println(Modules.getModuleFullName(core));
//        System.out.println(Modules.getModuleFullName(moduleA));
//        System.out.println(Modules.getModuleFullName(moduleB));
//        System.out.println(Modules.getModuleFullName(extension));
//        System.out.println(Modules.getModuleFullName(module1));

        b = Bean4.getInstance();
        //b = new Bean();

        String[] sArr = {"jedna", "dva", "tri", "ctyri", "pet", "sest"};
        SelectionSetModel<String> setModel = new SelectionSetModel<String>(sArr);
        b.setModel(setModel);

        DefaultPublishingContext context = new DefaultPublishingContext(new DefaultContext());

        JFrame f = new JFrame("Table test");
        f.getContentPane().setLayout(new BorderLayout());

//        System.out.println("b.getArray() = " + Arrays.toString(b.getArray()));
        //System.out.println("b.getSelectorChoice() = " + b.getSelectorChoice());

//        PropertyTable table = new PropertyTable(Modules.getModule("extension/module1").getComponent("bean2"));

/*        String [] sArr = {"jedna", "dve", "tri", "ctyri", "pet"};
        SelectionSetModel<String> selset = new SelectionSetModel<String>(sArr);

        java.util.List<String> l = new ArrayList<String>();
        l.add(sArr[0]);l.add(sArr[1]);l.add(sArr[2]);l.add(sArr[3]);l.add(sArr[4]);

        context.register(String.class, l, "model");*/

        //b.setModel(selset);

        PropertyTable table = new PropertyTable(b, new AnnotationPropertyExtractor(context));
        table.setRowHeight(25);

//        b.setStringValue("Blablabla");
        table.firePropertyChange("Choose from a set", true, true);

        f.add(new JScrollPane(table), BorderLayout.CENTER);
        f.setSize(400, 300);
        f.pack();
        f.setVisible(true);

        f.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
//                b = Bean4.getInstance();
                System.out.println("b.getSelectorChoice() = " + b.getSelectorChoice());
                System.out.printf("file: %s\n", b.getPath());
                System.out.printf("Retezec %s \n", b.getStringValue());


                String[] s = b.getModel().getAllElements();
                boolean[] en = b.getModel().getStateOfElements();
                for (int i = 0; i < s.length; i++) {
                    System.out.printf("Element: %s -- %s\n", s[i], en[i] ? "enabled" : "disabled");
                }

                System.exit(0);
            }
        });


//        File config = new File(Modules.getModuleFullName(module1) + "bean2" + ".properties");
//        System.out.println(config.getAbsolutePath());

//        ConfigurationManager manager = new AnnotationConfigurationManager(new CommonsConfigurationProvider(new File(Modules.getModuleFullName(module1))));
//        manager.configure(module1);
    }
}
