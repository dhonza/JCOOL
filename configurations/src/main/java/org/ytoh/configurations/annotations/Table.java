/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.ytoh.configurations.ui.ComponentNameRenderer;
import org.ytoh.configurations.ui.PropertyTableEditor;

/**
 * Edit as a Component using a Table.
 *
 * @author ytoh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Editor(component=PropertyTableEditor.class)
@Renderer(component=ComponentNameRenderer.class)
public @interface Table { }
