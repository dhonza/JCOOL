/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui;

import cz.cvut.felk.cig.jcool.ui.model.FunctionDetail;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 *
 * @author ytoh
 */
public class FunctionView {
    private JFrame frame;
    private JLabel title;
    private JTextPane fullDescription;
    private JLabel rangeLabel;
    private JLabel parameterLabel;
    private JPanel rangePanel;
    private JPanel parameterPanel;

    public JFrame getFrame(FunctionDetail detail) {
        title.setText(detail.getName());
        fullDescription.setText(detail.getDescription());
        this.setParameters(detail);
        this.setRanges(detail);
        frame.pack();
        return frame;
    }

    public void init() {
        title = new JLabel();
        title.setFont(new Font("Arial", Font.PLAIN, 20));

        JLabel image = new JLabel();
        image.setIcon(new ImageIcon("function_plot.png"));
        image.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        fullDescription = new JTextPane();
        JScrollPane fullDescriptionScrollPane = new JScrollPane();

        rangeLabel = new JLabel("Ranges");
        JSeparator rangeSeparator = new JSeparator();

        parameterLabel = new JLabel("Parameters");
        JSeparator parameterSeparator = new JSeparator();

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        fullDescription.setEditable(false);
        fullDescriptionScrollPane.setViewportView(fullDescription);

        fullDescription.setContentType("text/html");
        fullDescription.setBackground(frame.getBackground());
        fullDescription.setBorder(null);
        fullDescriptionScrollPane.setBorder(null);

        rangePanel = new JPanel();
        parameterPanel = new JPanel();

        frame.getRootPane().registerKeyboardAction(new EscapeListener(), KeyStroke.getKeyStroke("ESCAPE"), 0);

        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(10, 10, 10)
                    .add(layout.createParallelGroup(GroupLayout.LEADING)
                        .add(GroupLayout.TRAILING, fullDescriptionScrollPane, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                        .add(title)
                        .add(image)
                        .add(layout.createSequentialGroup()
                            .add(parameterLabel)
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(parameterSeparator, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                        .add(parameterPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .add(layout.createSequentialGroup()
                            .add(rangeLabel)
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(rangeSeparator, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                        .add(rangePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .add(10, 10, 10)));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(5, 5, 5)
                    .add(title)
                    .add(3, 3, 3)
                    .add(image)
                    .addPreferredGap(LayoutStyle.UNRELATED)
                    .add(fullDescriptionScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.RELATED)
                    .add(layout.createParallelGroup(GroupLayout.TRAILING)
                        .add(parameterLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .add(parameterSeparator, GroupLayout.DEFAULT_SIZE, 10, GroupLayout.PREFERRED_SIZE))
                    .add(parameterPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(layout.createParallelGroup(GroupLayout.TRAILING)
                        .add(rangeLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .add(rangeSeparator, GroupLayout.DEFAULT_SIZE, 10, GroupLayout.PREFERRED_SIZE))
                    .add(rangePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.RELATED, 5, Short.MAX_VALUE)
                    .add(10, 10, 10)
                ));
    }
    
    private void setParameters(FunctionDetail detail) {
        String[] headers = new String[] {"Property", "Description"};
        String[][] values = new String[detail.getProperties().length][2];

        for (int i = 0; i < values.length; i++) {
            values[i][0] = detail.getProperties()[i].getName();
            values[i][1] = detail.getProperties()[i].getDescription();
        }

        JTable params = new JTable(values, headers);

        params.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        params.setEnabled(false);
        parameterPanel.setLayout(new BorderLayout());
        parameterPanel.add(params.getTableHeader(), BorderLayout.PAGE_START);
        parameterPanel.add(params, BorderLayout.CENTER);
    }

    private void setRanges(FunctionDetail detail) {
        String[] headers = new String[] {"Dimension", "Minimum", "Maximum"};
        String[][] values = new String[detail.getDimension()][3];

        for (int i = 0; i < values.length; i++) {
            values[i][0] = String.valueOf(i + 1);
            values[i][1] = String.valueOf(detail.getMinimum(i));
            values[i][2] = String.valueOf(detail.getMaximum(i));
        }

        JTable ranges = new JTable(values, headers);

        ranges.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        ranges.setEnabled(false);
        rangePanel.setLayout(new BorderLayout());
        rangePanel.add(ranges.getTableHeader(), BorderLayout.PAGE_START);
        rangePanel.add(ranges, BorderLayout.CENTER);
    }

    private class EscapeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
        }
    }
}
