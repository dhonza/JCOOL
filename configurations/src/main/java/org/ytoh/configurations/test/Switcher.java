/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.test;

import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;

/**
 *
 * @author ytoh
 */
@Component
public class Switcher {
    @org.ytoh.configurations.annotations.Property(name="ON switch")
    private boolean on = true;
    
    @org.ytoh.configurations.annotations.Property(name="Side-effect")
    private String o = "ON!";

    public Switcher() { }

    public PropertyState getOState() {
        return on ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
        this.o = on ? "ON!" : "OFF!";
    }

    @Override
    public String toString() {
        return String.format("[ %b, %s ]", on, o);
    }
}
