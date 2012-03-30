/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.test;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.ytoh.configurations.context.DefaultContext;
import org.ytoh.configurations.context.DefaultPublishingContext;
import org.ytoh.configurations.ui.PropertyTable;
import org.ytoh.configurations.util.AnnotationPropertyExtractor;

/**
 *
 * @author ytoh
 */
public class Boot2 {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("a", "b", "c");
        List<String> trainers = Arrays.asList("linear", "polynomial");
        List<Integer> integers = Arrays.asList(1, 2, 3);
        DefaultPublishingContext context = new DefaultPublishingContext(new DefaultContext());
        context.register(String.class, strings, "key");
        context.register(Integer.class, integers, "");
        context.register(String.class, trainers, "key1");
        DynamicBean dynaBean = new DynamicBean();

        JFrame f = new JFrame("Table test");
        f.getContentPane().setLayout(new BorderLayout());

        PropertyTable table = new PropertyTable(dynaBean, new AnnotationPropertyExtractor(context));
        table.setRowHeight(25);

        f.add(new JScrollPane(table), BorderLayout.CENTER);
        f.setSize(400, 300);
        f.pack();
        f.setVisible(true);

        f.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
