package org.ytoh.configurations.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.ytoh.configurations.annotations.Component;

/**
 * <p>Utility class for runtime processing of {@link Annotation}s.</p>
 *
 * @author ytoh
 */
public final class Annotations {

    /**
     * <p>Retrieves from the supplied {@link AnnotatedElement} all annotations
     * annotated with the <code>type</code> metaannotation.</p>
     * 
     * @param type of the metaannotation to look for
     * @param element holding annotations to scan
     * @return a list of {@link Annotation}s that are annotated with the specified
     * metaannotation
     */
    public static List<Annotation> like(final Class<? extends Annotation> type, final AnnotatedElement element) {
        return (List<Annotation>) CollectionUtils.select(Arrays.asList(element.getAnnotations()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Annotation)o).annotationType().isAnnotationPresent(type);
            }
        });
    }

    /**
     * <p>Filters the list of {@link AnnotatedElement}s leaving only elements
     * annotated with {@link Annotation} of type <code>type</code>.</p>
     *
     * @param elements list of elements to filter
     * @param type type of the annotation to look for
     * @return a list of <code>AnnotatedElement</code>s that are annotated
     * with <code>type</code>
     */
    public static <T extends AnnotatedElement> List<T> filter(final List<T> elements, final Class<? extends Annotation> type) {
        return (List<T>) CollectionUtils.select(elements, new Predicate() {

            public boolean evaluate(Object o) {
                return ((AnnotatedElement)o).isAnnotationPresent(type);
            }
        });
    }

    /**
     * Retrieves the component name for <code>type</code>.
     *
     * <p>The name of a component is specified in {@link Component#name}. If
     * the <code>Component</code> annotation is not present on the supplied type
     * the type's simple name is returned.</p>
     *
     * @param component class to extract component name from
     * @return <code>String</code> containing the component name
     */
    public static String getName(final Class<?> component) {
        if(component.isAnnotationPresent(Component.class)) {
            String name = component.getAnnotation(Component.class).name();

            if(name.length() > 0) { // if the default has been changed
                return name;
            }
        }

        return component.getSimpleName();
    }
}
