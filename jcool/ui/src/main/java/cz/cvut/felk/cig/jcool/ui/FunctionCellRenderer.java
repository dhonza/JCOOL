/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui;

import cz.cvut.felk.cig.jcool.ui.model.FunctionDetail;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
public class FunctionCellRenderer extends JPanel implements TableCellRenderer {

    private JLabel icon;
    private JLabel title;
    private JLabel parametersLabel;
    private JTextArea description;
    private JPanel descriptionPanel;
    private JPanel parameters;
    private JList parameterList;
    private Color evenRowColor = Color.white;
    private Color oddRowColor = new Color(235, 235, 235);
    int insets;

    public FunctionCellRenderer() {
        icon = new JLabel();
        title = new JLabel();
        parametersLabel = new JLabel("Properties:");
        parametersLabel.setFont(new Font(parametersLabel.getFont().getFontName(), Font.BOLD, 12));
        description = new JTextArea();
        descriptionPanel = new JPanel();
        parameters = new JPanel();
        parameterList = new JList();

        GroupLayout parameterLayout = new GroupLayout(parameters);
        parameters.setLayout(parameterLayout);
        parameterLayout.setHorizontalGroup(parameterLayout.createSequentialGroup()
                .add(parametersLabel)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(parameterList, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

        parameterLayout.setVerticalGroup(parameterLayout.createParallelGroup()
                .add(parametersLabel)
                .add(parameterList, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

        title.setOpaque(true);

        description.setBorder(null);
        descriptionPanel.setBorder(null);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        // windows rendering fix
        description.setFont(title.getFont());
        // windows rendering fix - end

        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 18));

        GroupLayout descriptionLayout = new GroupLayout(descriptionPanel);
        descriptionPanel.setLayout(descriptionLayout);

        descriptionLayout.setHorizontalGroup(descriptionLayout.createSequentialGroup()
                .add(icon)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(descriptionLayout.createParallelGroup(GroupLayout.LEADING)
                    .add(title)
                    .add(description, 0, 100, Short.MAX_VALUE)));

        descriptionLayout.setVerticalGroup(descriptionLayout.createParallelGroup(GroupLayout.LEADING)
                .add(icon)
                .add(descriptionLayout.createSequentialGroup()
                    .add(title)
                    .add(description, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING)
                .add(descriptionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .add(parameters, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .add(descriptionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(parameters, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        insets = this.getInsets().top + this.getInsets().bottom;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        FunctionDetail detail = (FunctionDetail) value;

        icon.setIcon(new ImageIcon(detail.getIcon()));
        title.setText(detail.getName());
        description.setText(detail.getShortDescription());
        DefaultListModel parameterModel = new DefaultListModel();
        for (Property p: detail.getProperties()) {
            parameterModel.addElement(p.getName());
        }
        parameterList.setModel(parameterModel);

        Color foregroundColor = null;
        Color backgroundColor = null;

        if (isSelected) {
            foregroundColor = table.getSelectionBackground();
            backgroundColor = table.getSelectionForeground();

            parameters.setVisible(true);

            if (table.getRowHeight(row) != (int) (insets + descriptionPanel.getPreferredSize().getHeight() + parameters.getPreferredSize().getHeight())) {
                table.setRowHeight(row, (int) (insets + descriptionPanel.getPreferredSize().getHeight() + parameters.getPreferredSize().getHeight()));
            }
        } else {
            Color c = ((row & 0x1) == 1) ? evenRowColor : oddRowColor;

            foregroundColor = c;
            backgroundColor = table.getForeground();

            parameters.setVisible(false);
            if (table.getRowHeight(row) != (int) (insets + descriptionPanel.getPreferredSize().getHeight())) {
                table.setRowHeight(row, (int) (insets + descriptionPanel.getPreferredSize().getHeight()));
            }
        }

        setBackground(foregroundColor);
        setForeground(backgroundColor);

        descriptionPanel.setBackground(foregroundColor);
        descriptionPanel.setForeground(backgroundColor);

        title.setBackground(foregroundColor);
        title.setForeground(backgroundColor);

        description.setBackground(foregroundColor);
        description.setForeground(backgroundColor);

        parameters.setBackground(foregroundColor);
        parameters.setForeground(backgroundColor);

        parameterList.setBackground(foregroundColor);
        parameterList.setForeground(backgroundColor);

        parametersLabel.setBackground(foregroundColor);
        parametersLabel.setForeground(backgroundColor);

        return this;
    }
}
