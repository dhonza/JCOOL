package org.ytoh.configurations.annotations;

import org.ytoh.configurations.ui.FileDirectoryPickerEditor;
import org.ytoh.configurations.ui.FileDirectoryPickerRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Oct 7, 2009
 * Time: 5:08:40 PM
 * To change this template use File | Settings | File Templates.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Editor(component = FileDirectoryPickerEditor.class)
@Renderer(component = FileDirectoryPickerRenderer.class)


public @interface FileDirectoryPicker {

    String value();

    /**
     * Directories may be selected
     * @return Directories may be selected
     */
    boolean allowDirectories() default false;

    /**
     * Files may be selected
     * @return Files may be selected
     */
    boolean allowFiles() default true;

    /**
     * Selected path (file or directory) must exist
     * @return Selected path (file or directory) must exist
     */
    boolean pathMustExist() default true;

    /**
     * Several files may be selected
     * @return Several files may be selected
     */
    boolean multipleFilesAllowed() default false;

    /**
     * Title of dialog
     * @return Title of dialog
     */
    String title() default "Select file or directory";
}
