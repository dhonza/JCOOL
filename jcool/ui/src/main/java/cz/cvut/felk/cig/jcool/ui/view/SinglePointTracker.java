/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.view;

import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.Producer;
import cz.cvut.felk.cig.jcool.core.ValuePoint;
import cz.cvut.felk.cig.jcool.core.ValuePointTelemetry;
import cz.cvut.felk.cig.jcool.experiment.TelemetryVisualization;
import cz.cvut.felk.cig.jcool.experiment.Iteration;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import org.apache.commons.collections.Closure;
import org.apache.log4j.Logger;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.InterpolatedPlot;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 * A visualization implementation able to display a succession of values from
 * a certain optimization run in time.
 *
 * <p>This implementation takes accepts instances of {@link ValuePointTelemetry}
 * from which it produces a representation of the currently analysed point is
 * located in the finction space. It renders a landscape representing the function
 * value onto which it then plots the analysed points.</p>
 *
 * <p>From function with more then two dimensions it allows the user to select
 * any two of the dimensions in which to track the point. The user can do this
 * by selecting the dimension for the X and Y axes.</p>
 *
 * <p>This implementation also allows the user to compare two or more runs of
 * an optimization method on a function by retaining the points of the previous
 * runs.</p>
 *
 * @author ytoh
 */
@Component(name = "Show current position", description = "By sampling the input function " +
"this visualization created a landscape of function values and then tracks " +
"the movement of the optimization method through it.")
public class SinglePointTracker implements TelemetryVisualization<ValuePointTelemetry> {

    static Logger logger = Logger.getLogger(SinglePointTracker.class);

    /**
     * Marked container for optimization runs.
     *
     * <p>Every run has its own container of points and its own vertex marker.
     * It also can be switched on and off.</p>
     */
    public enum Slot {

        CIRCLE, SQUARE, CROSS, TRIANGLE;

        private final List<ValuePoint> points = new CopyOnWriteArrayList<ValuePoint>();
        private boolean show;

        public boolean isShow() {
            return show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public List<ValuePoint> getPoints() {
            return points;
        }

        public void clearList() {
            points.clear();
        }
    }

    @Property(name = "Resolution", description = "Resolution defines how fine grained function sampling will be (the higher the better)")
    @Range(from = 1, to = 1000)
    private int resolution = 200;

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }
    @Property(name = "Number of points", description = "The number of points in the trail to render")
    @Range(from = 1, to = Integer.MAX_VALUE)
    private int maxPoints = 15;

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }
    @Property(name = "Buffer", description = "How much space should there be between a point and the edge")
    @Range(from = 0, to = Integer.MAX_VALUE)
    private double buffer = 5;

    public double getBuffer() {
        return buffer;
    }

    public void setBuffer(double buffer) {
        this.buffer = buffer;
    }
    @Property(name = "Center X", description = "X coordinate of center point at visualization start")
    private double centerX = 0;

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }
    @Property(name = "Center Y", description = "Y coordinate of center point at visualization start")
    private double centerY = 0;

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    @Property(name="Use mark")
    private boolean useMark;

    public boolean isUseMark() {
        return useMark;
    }

    public void setUseMark(boolean useMark) {
        this.useMark = useMark;
    }

    @Property(name = "Mark X", description = "X coordinate of marker point at visualization start")
    private double startX;

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public PropertyState getStartXState() {
        return useMark ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    @Property(name = "Mark Y", description = "Y coordinate of marker point at visualization start")
    private double startY;

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public PropertyState getStartYState() {
        return useMark ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    @Property(name = "Show circle slot")
    private boolean showCircleSlot;

    public boolean isShowCircleSlot() {
        return Slot.CIRCLE.isShow();
    }

    public void setShowCircleSlot(boolean showCircleSlot) {
        Slot.CIRCLE.setShow(showCircleSlot);
    }

    public PropertyState getShowCircleSlotState() {
        return Slot.CIRCLE.getPoints().isEmpty() ? PropertyState.DISABLED : PropertyState.ENABLED;
    }

    @Property(name = "Show square slot")
    private boolean showSquareSlot;

    public boolean isShowSquareSlot() {
        return Slot.SQUARE.isShow();
    }

    public void setShowSquareSlot(boolean showSquareSlot) {
        Slot.SQUARE.setShow(showSquareSlot);
    }

    public PropertyState getShowSquareSlotState() {
        return Slot.SQUARE.getPoints().isEmpty() ? PropertyState.DISABLED : PropertyState.ENABLED;
    }

    @Property(name = "Show triangle slot")
    private boolean showTriangleSlot;

    public boolean isShowTriangleSlot() {
        return Slot.TRIANGLE.isShow();
    }

    public void setShowTriangleSlot(boolean showTriangleSlot) {
        Slot.TRIANGLE.setShow(showTriangleSlot);
    }

    public PropertyState getShowTriangleSlotState() {
        return Slot.TRIANGLE.getPoints().isEmpty() ? PropertyState.DISABLED : PropertyState.ENABLED;
    }

    @Property(name = "Show x slot")
    private boolean showCrossSlot;

    public boolean isShowCrossSlot() {
        return Slot.CROSS.isShow();
    }

    public void setShowCrossSlot(boolean showCrossSlot) {
        Slot.CROSS.setShow(showCrossSlot);
    }

    public PropertyState getShowCrossSlotState() {
        return Slot.CROSS.getPoints().isEmpty() ? PropertyState.DISABLED : PropertyState.ENABLED;
    }

    @Property(name = "Use slot")
    private Slot useSlot = Slot.CIRCLE;

    public Slot getUseSlot() {
        return useSlot;
    }

    public void setUseSlot(Slot useSlot) {
        this.useSlot = useSlot;
    }

    // Private data
    private GridPointData data;
    private InterpolatedPlot plot;
    private JPanel panel;
    private DrawingPanel drawablePanel;
    private TreeSet<ValuePoint> xAxis = new TreeSet<ValuePoint>(new Comparator<ValuePoint>() {

        public int compare(ValuePoint o1, ValuePoint o2) {
            double xCoordinate1 = o1.getPoint().toArray()[0];
            double xCoordinate2 = o2.getPoint().toArray()[0];

            if (xCoordinate1 == xCoordinate2) {
                return 0;
            } else {
                if (xCoordinate1 < xCoordinate2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    });
    private TreeSet<ValuePoint> yAxis = new TreeSet<ValuePoint>(new Comparator<ValuePoint>() {

        public int compare(ValuePoint o1, ValuePoint o2) {
            double yCoordinate1 = o1.getPoint().toArray()[1];
            double yCoordinate2 = o2.getPoint().toArray()[1];

            if (yCoordinate1 == yCoordinate2) {
                return 0;
            } else {
                if (yCoordinate1 < yCoordinate2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    });
    private Function lastFunction = null;

    //private Map<Point,Double> cache = new HashMap<Point, Double>(10000);
    private Closure datasetRecreation = new Closure() { // NullObject

        public void execute(Object input) {
        }
    };

    public void init(final Function function) {
        xAxis.clear();
        yAxis.clear();

        if (function != lastFunction) { // need to reset
            Slot.CIRCLE.clearList();
            Slot.CROSS.clearList();
            Slot.SQUARE.clearList();
            Slot.TRIANGLE.clearList();
        }

        data = new GridPointData(resolution, resolution, 1);
        plot = new InterpolatedPlot(data);

        useSlot.clearList();
        final List<ValuePoint> points = useSlot.getPoints();
        for (Slot slot : Slot.values()) {
            if(slot.isShow()) {
                for (ValuePoint valuePoint : slot.getPoints()) {
                    xAxis.add(valuePoint);
                    yAxis.add(valuePoint);
                }
            }
        }

        // clean drawing panel
        drawablePanel.removeDrawables(InterpolatedPlot.class);
        drawablePanel.addDrawable(plot);

        datasetRecreation = new Closure() {

            public void execute(Object input) {
                ValuePoint vp = (ValuePoint) input;
                points.add(vp);
                xAxis.add(vp);
                yAxis.add(vp);

                if (points.size() > maxPoints) {
                    ValuePoint removed = points.remove(0);
                    xAxis.remove(removed);
                    yAxis.remove(removed);
                }

                // for ilustration
                if (xAxis.size() > 0 && yAxis.size() > 0) {
                    double lowerXBound = Math.floor(xAxis.first().getPoint().toArray()[0] - buffer);
                    double upperXBound = Math.ceil(xAxis.last().getPoint().toArray()[0] + buffer);
                    double lowerYBound = Math.floor(yAxis.first().getPoint().toArray()[1] - buffer);
                    double upperYBound = Math.ceil(yAxis.last().getPoint().toArray()[1] + buffer);

                    data.setScale(lowerXBound, upperXBound, lowerYBound, upperYBound);
                    double[][][] xyz = data.getData();

                    for (int i = 0; i < resolution; i++) {
                        for (int j = 0; j < resolution; j++) {
                            Point p = Point.at(xyz[i][j]);
                            // check the cached value of the point
                            xyz[i][j][2] = /*cache.containsKey(p) ? cache.get(p) :*/ function.valueAt(p);
                        }
                    }
                }

                plot.setGridData(data);

                drawablePanel.repaint();
            }
        };

        lastFunction = function;

        if(useMark) {
            datasetRecreation.execute(ValuePoint.at(Point.at(startX, startY), function));
        }

        // initialize to starting point
        double lowerXBound = Math.floor(Math.min(startX, centerX) - buffer);
        double upperXBound = Math.ceil(Math.max(startX, centerX) + buffer);
        double lowerYBound = Math.floor(Math.min(startY, centerY) - buffer);
        double upperYBound = Math.ceil(Math.max(startY, centerY) + buffer);

        data.setScale(lowerXBound, upperXBound, lowerYBound, upperYBound);
        double[][][] xyz = data.getData();

        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                Point p = Point.at(xyz[i][j]);
                // check the cached value of the point
                xyz[i][j][2] = /*cache.containsKey(p) ? cache.get(p) :*/ function.valueAt(p);
            }
        }

        plot.setGridData(data);
        drawablePanel.repaint();
    }

    public void attachTo(JPanel panel) {
        this.panel = panel;
        panel.removeAll();

        panel.setLayout(new BorderLayout());

        drawablePanel = new PointDrawingPanel();

        panel.add(drawablePanel, BorderLayout.CENTER);
    }

    public Class<ValuePointTelemetry> getAcceptableType() {
        return ValuePointTelemetry.class;
    }

    public void notifyOf(Producer<? extends Iteration<ValuePointTelemetry>> producer) {
        Iteration<ValuePointTelemetry> i = producer.getValue();

        if (i != null && i.getValue() != null) {
            datasetRecreation.execute(i.getValue().getValue());
        }
    }

    /**
     *
     */
    private class PointDrawingPanel extends DrawingPanel {

        private final int RADIUS = 6;
        private final Stroke LINE = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        private final Color PREVIOUS = Color.WHITE;

        public PointDrawingPanel() {
            setSquareAspect(true);
            enableInspector(true);
            setShowCoordinates(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            for (Slot slot : Slot.values()) {
                if(slot.isShow() && slot != useSlot) {
                    paintSequence(slot, slot.getPoints(), Color.BLACK, g2d);
                }
            }

            paintSequence(useSlot, useSlot.getPoints(), Color.BLACK, g2d);
        }

        /**
         *
         * @param points
         * @param color
         * @param g
         */
        private void paintSequence(Slot type, List<ValuePoint> points, Color color, Graphics2D g) {
            int i = 0;
            int size = points.size();
            int lastX = 0;
            int lastY = 0;

            if (size > 0) {
                double increment = 200 / size;

                for (ValuePoint vp : points) {
                    int alpha = (int) (i * increment) + 55;
                    Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

                    double[] coordinates = vp.getPoint().toArray();
                    int x = xToPix(coordinates[0]);
                    int y = yToPix(coordinates[1]);

                    if (i > 0) {
                        g.setColor(c);
                        g.setStroke(LINE);
                        g.drawLine(lastX, lastY, x, y);
                    }

                    lastX = x;
                    lastY = y;

                    g.setColor(c);
                    g.setStroke(LINE);

                    switch (type) {
                        case CIRCLE:
                            g.drawOval(x - RADIUS / 2 - 3, y - RADIUS / 2 - 3, RADIUS * 2, RADIUS * 2);
                            break;
                        case SQUARE:
                            g.drawRect(x - RADIUS / 2 - 2, y - RADIUS / 2 - 2, RADIUS * 2 - 2, RADIUS * 2 - 2);
                            break;
                        case TRIANGLE:
                            GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
                            polyline.moveTo(x, y - RADIUS / 2 - 4);
                            polyline.lineTo(x + RADIUS / 2 + 3, y + RADIUS / 2 + 1);
                            polyline.lineTo(x - RADIUS / 2 - 3, y + RADIUS / 2 + 1);
                            polyline.closePath();
                            g.draw(polyline);
                            break;
                        case CROSS:
                            int xLow = x - RADIUS / 2 - 1;
                            int xHigh = x + RADIUS / 2 + 1;
                            int yLow = y - RADIUS / 2 - 1;
                            int yHigh = y + RADIUS / 2 + 1;
                            g.drawLine(xLow, yLow, xHigh, yHigh);
                            g.drawLine(xHigh, yLow, xLow, yHigh);
                            break;
                    }
                    i++;
                }
            }
        }
    }
}
