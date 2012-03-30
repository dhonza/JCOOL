/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.test;

import org.ytoh.configurations.annotations.Property;

/**
 *
 * @author ytoh
 */
public class Visualization {
    @Property(name="Show telemetry:")
    private boolean showTelemetry;

    @Property(name="Main visualization:")
    private Type visualization1;

    @Property(name="Secondary visualization:")
    private Type visualization2;

    @Property(name="Layout:")
    private Layout layout;

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public boolean isShowTelemetry() {
        return showTelemetry;
    }

    public void setShowTelemetry(boolean showTelemetry) {
        this.showTelemetry = showTelemetry;
    }

    public Type getVisualization1() {
        return visualization1;
    }

    public void setVisualization1(Type visualization1) {
        this.visualization1 = visualization1;
    }

    public Type getVisualization2() {
        return visualization2;
    }

    public void setVisualization2(Type visualization2) {
        this.visualization2 = visualization2;
    }

    public static enum Type {
        VISUALIZATION_1,VISUALIZATION_2,VISUALIZATION_3
    }

    public static enum Layout {
        MAIN_ONLY, EQUIVALENT, SMALL, TABBED
    }
}
