/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.view;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author ytoh
 */
public class ExperimentLog extends JPanel {
    private JTextArea textArea;

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        textArea.setEditable(false);
        textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));

        JScrollPane scrollLog = new JScrollPane(textArea);
        scrollLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollLog, BorderLayout.CENTER);
    }
}
