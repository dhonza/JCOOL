package org.ytoh.configurations.ui;

import org.ytoh.configurations.annotations.Editor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Sep 29, 2009
 * Time: 4:32:12 PM
 * To change this template use File | Settings | File Templates.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Editor(component = DynamicListPickerEditor.class)

public @interface DynamicListPicker {
    String [] items();
    boolean [] selected();
}
