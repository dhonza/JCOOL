/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui;

import cz.cvut.felk.cig.jcool.core.StopCondition;
import cz.cvut.felk.cig.jcool.ui.model.OptimizationMethodDetail;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.ytoh.configurations.Property;

/**
 *
 * @author ytoh
 */
public class MethodCellRenderer extends JPanel implements TableCellRenderer {

    private JLabel      title;
    private JTextArea   description;
    private JPanel      descriptionPanel;
    private JPanel      detailsPanel;
    private JLabel      parameterLabel;
    private JLabel      stopConditionLabel;
    private JList       parameters;
    private JList       stopConditions;
    private Color evenRowColor = Color.white;
    private Color oddRowColor = new Color(235, 235, 235);
    private int insets;

    public MethodCellRenderer() {
        title = new JLabel();
        
        description = new JTextArea();
        descriptionPanel = new JPanel();

        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        // windows rendering fix
        description.setFont(title.getFont());
        // windows rendering fix - end
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 18));

        GroupLayout descriptionLayout = new GroupLayout(descriptionPanel);
        descriptionPanel.setLayout(descriptionLayout);
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        descriptionLayout.setHorizontalGroup(descriptionLayout.createParallelGroup(GroupLayout.LEADING)
                .add(title)
                .add(description, 0, 100, Short.MAX_VALUE));

        descriptionLayout.setVerticalGroup(descriptionLayout.createSequentialGroup()
                .add(title)
                .add(description, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

        parameterLabel = new JLabel("Parameters:");
        parameterLabel.setFont(new Font(parameterLabel.getFont().getFontName(), Font.BOLD, 12));

        stopConditionLabel = new JLabel("Stop conditions:");
        stopConditionLabel.setFont(new Font(stopConditionLabel.getFont().getFontName(), Font.BOLD, 12));
        parameters = new JList();
        stopConditions = new JList();
        detailsPanel = new JPanel();

        detailsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        GroupLayout detailsLayout = new GroupLayout(detailsPanel);
        detailsPanel.setLayout(detailsLayout);

        detailsLayout.setHorizontalGroup(detailsLayout.createSequentialGroup()
                .add(parameterLabel).addPreferredGap(LayoutStyle.RELATED)
                .add(parameters, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addPreferredGap(LayoutStyle.UNRELATED)
                .add(stopConditionLabel).addPreferredGap(LayoutStyle.RELATED)
                .add(stopConditions, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

        detailsLayout.setVerticalGroup(detailsLayout.createParallelGroup(GroupLayout.HORIZONTAL)
                .add(parameterLabel)
                .add(parameters, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .add(stopConditionLabel)
                .add(stopConditions, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING)
                .add(descriptionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .add(detailsPanel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .add(descriptionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(detailsPanel, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE));

        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        insets = this.getInsets().top + this.getInsets().bottom;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        OptimizationMethodDetail detail = (OptimizationMethodDetail) value;

        title.setText(detail.getName());
        description.setText(detail.getShortDescription());
        
        DefaultListModel parameterModel = new DefaultListModel();
        for (Property p: detail.getProperties()) {
            parameterModel.addElement(p.getName());
        }
        parameters.setModel(parameterModel);

        DefaultListModel stopConditionModel = new DefaultListModel();
        for(StopCondition sc: detail.getStopConditions()) {
            stopConditionModel.addElement(sc.getClass().getSimpleName());
        }
        stopConditions.setModel(stopConditionModel);

        Color foregroundColor = null;
        Color backgroundColor = null;

        if(isSelected) {
            foregroundColor = table.getSelectionBackground();
            backgroundColor = table.getSelectionForeground();

            detailsPanel.setVisible(true);

            if (table.getRowHeight(row) != (int) (insets + descriptionPanel.getPreferredSize().getHeight() + detailsPanel.getPreferredSize().getHeight())) {
                table.setRowHeight(row, (int) (insets + descriptionPanel.getPreferredSize().getHeight() + detailsPanel.getPreferredSize().getHeight()));
            }
        } else {
            Color c = ((row & 0x1) == 1) ? evenRowColor : oddRowColor;

            foregroundColor = c;
            backgroundColor = table.getForeground();

            detailsPanel.setVisible(false);

            if (table.getRowHeight(row) != (int) (insets + descriptionPanel.getPreferredSize().getHeight())) {
                table.setRowHeight(row, (int) (insets + descriptionPanel.getPreferredSize().getHeight()));
            }
        }

        this.setBackground(foregroundColor);
        this.setForeground(backgroundColor);

        descriptionPanel.setBackground(foregroundColor);
        descriptionPanel.setForeground(backgroundColor);

        detailsPanel.setBackground(foregroundColor);
        detailsPanel.setForeground(backgroundColor);

        title.setBackground(foregroundColor);
        title.setForeground(backgroundColor);

        description.setBackground(foregroundColor);
        description.setForeground(backgroundColor);

        parameterLabel.setBackground(foregroundColor);
        parameterLabel.setForeground(backgroundColor);

        parameters.setBackground(foregroundColor);
        parameters.setForeground(backgroundColor);

        stopConditionLabel.setBackground(foregroundColor);
        stopConditionLabel.setForeground(backgroundColor);

        stopConditions.setBackground(foregroundColor);
        stopConditions.setForeground(backgroundColor);

        return this;
    }
}
