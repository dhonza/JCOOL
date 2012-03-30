/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.test;

import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.ui.DynamicDropDown;

/**
 *
 * @author ytoh
 */
@Component(name="Dynamic bean")
public class DynamicBean {

    @Property(name="Dynamic string")
    @DynamicDropDown(type=String.class,key="key")
    private String value;

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Property(name="Dynamic string")
    @DynamicDropDown(type=String.class,key="key1")
    private String value2;
    @Property(name="Dynamic integer")
    @DynamicDropDown(type=Integer.class)
    private int[] i = new int[1];

    public int[] getI() {
        return i;
    }

    public void setI(int[] i) {
        this.i = i;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
