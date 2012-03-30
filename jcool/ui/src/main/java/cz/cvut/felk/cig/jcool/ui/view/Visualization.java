/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.ui.model.Visualization.Type;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author ytoh
 */
public class Visualization extends JPanel implements PropertyChangeListener {

    // composition
    private JPanel telemetry;
    private JPanel primaryVisualization;
    private JPanel secondaryVisualization;
    private JPanel visualizationControls;
    private JLayeredPane pane;

    // model
    private cz.cvut.felk.cig.jcool.ui.model.Visualization visualization;

    // layout
    private Position  primaryVisualizationPosition = Position.FULL;
    private Position  secondaryVisualizationPosition = Position.TOP_RIGHT;
    private Position  visualizationControlsPosition = Position.BOTTOM;
    private Position  telemetryPosition = Position.TOP_LEFT;

    public void setVisualizationControls(JPanel visualizationControls) {
        this.visualizationControls = visualizationControls;
    }

    public void setVisualization(cz.cvut.felk.cig.jcool.ui.model.Visualization visualization) {
        this.visualization = visualization;
        this.visualization.addPropertyChangeListener(this);
    }

    public void setPrimaryVisualization(JPanel primaryVisualization) {
        this.primaryVisualization = primaryVisualization;
    }

    public void setSecondaryVisualization(JPanel secondaryVisualization) {
        this.secondaryVisualization = secondaryVisualization;
    }

    public void setTelemetry(JPanel telemetry) {
        this.telemetry = telemetry;
    }

    /**
     *
     */
    public void initComponents() {
        primaryVisualization.setBackground(Color.WHITE);
        secondaryVisualization.setVisible(false);
        secondaryVisualization.setPreferredSize(new Dimension(250, 250));
        secondaryVisualization.setBackground(new Color(235, 235, 235));
        visualizationControls.setBorder(BorderFactory.createEtchedBorder());

        pane = new JLayeredPane();

        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);

        pane.add(primaryVisualization, new Integer(0));
        pane.add(secondaryVisualization, new Integer(1));
        pane.add(telemetry, new Integer(2));
        pane.add(visualizationControls, new Integer(3));
        telemetry.setBorder(BorderFactory.createEtchedBorder());

        adjustComponent(secondaryVisualization, secondaryVisualizationPosition);
        adjustComponent(primaryVisualization, primaryVisualizationPosition);
        adjustComponent(telemetry, telemetryPosition);
        adjustComponent(visualizationControls, visualizationControlsPosition);
    }

    @Override
    public void repaint() {
        adjustComponent(pane, Position.FULL);
        adjustComponent(secondaryVisualization, secondaryVisualizationPosition);
        adjustComponent(primaryVisualization, primaryVisualizationPosition);
        adjustComponent(telemetry, telemetryPosition);
        adjustComponent(visualizationControls, visualizationControlsPosition);

        super.repaint();
    }

    public JPanel getTelemetry() {
        return telemetry;
    }

    public JPanel getPrimaryVisualization() {
        return primaryVisualization;
    }

    /**
     *
     * @param c
     * @param position
     */
    private void adjustComponent(JComponent c, Position position) {
        if (c == null) {
            return;
        }

        Rectangle preferredSize = super.getBounds();
        double width = preferredSize.getWidth();
        double height = preferredSize.getHeight();

        double cHeight = c.getPreferredSize().getHeight();
        double cWidth = c.getPreferredSize().getWidth();

        switch (position) {
            case FULL:
                c.setBounds(0, 0, (int) width, (int) (height - visualizationControls.getPreferredSize().getHeight()));
                break;
            case TOP_LEFT:
                c.setBounds(0, 0, (int) cWidth, (int) cHeight);
                break;
            case TOP_RIGHT:
                c.setBounds((int) (width - cWidth), 0, (int) cWidth, (int) cHeight);
                break;
            case BOTTOM_LEFT:
                c.setBounds(0, (int) (height - cHeight), (int) cWidth, (int) cHeight);
                break;
            case BOTTOM_RIGHT:
                c.setBounds((int) (width - cWidth), (int) (height - cHeight), (int) cWidth, (int) cHeight);
                break;
            case LEFT_HALF:
                c.setBounds(0, 0, (int) (width / 2), (int) height);
                break;
            case RIGHT_HALF:
                c.setBounds((int) (width / 2), 0, (int) (width / 2), (int) height);
                break;
            case BOTTOM:
                c.setBounds(0, (int) (height - cHeight) - 1, (int) width, (int) cHeight);
                break;
            default:
        }
    }

    /**
     *
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("show-telemetry".equals(evt.getPropertyName())) {
            telemetry.setVisible((Boolean) evt.getNewValue());
        } else if ("type".equals(evt.getPropertyName())) {
            Type type = (Type) evt.getNewValue();
            switch (type) {
                case NONE:
                    primaryVisualization.setVisible(false);
                    secondaryVisualization.setVisible(false);
                    break;
                case EQUAL:
                    primaryVisualization.setVisible(true);
                    secondaryVisualization.setVisible(true);
                    primaryVisualizationPosition = Position.LEFT_HALF;
                    secondaryVisualizationPosition = Position.RIGHT_HALF;
                    break;
                case PRIMARY_ONLY:
                    primaryVisualization.setVisible(true);
                    secondaryVisualization.setVisible(false);
                    primaryVisualizationPosition = Position.FULL;
                    break;
                case SECONDARY_ONLY:
                    primaryVisualization.setVisible(false);
                    secondaryVisualization.setVisible(true);
                    secondaryVisualizationPosition = Position.FULL;
                    break;
                case FAVOR_PRIMARY:
                    primaryVisualization.setVisible(true);
                    secondaryVisualization.setVisible(true);
                    primaryVisualizationPosition = Position.FULL;
                    secondaryVisualizationPosition = Position.TOP_RIGHT;
                    break;
                default:
            }
        }

        repaint();
        getParent().validate();
    }

    /**
     * 
     */
    private static enum Position {

        FULL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, LEFT_HALF, RIGHT_HALF, BOTTOM
    }
}
