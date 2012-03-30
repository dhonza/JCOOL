package org.ytoh.configurations.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.hibernate.validator.engine.ConstraintValidatorFactoryImpl;
import org.ytoh.configurations.*;
import org.ytoh.configurations.annotations.Editor;
import org.ytoh.configurations.annotations.Renderer;
import org.ytoh.configurations.context.Context;
import org.ytoh.configurations.context.DefaultContext;
import org.ytoh.configurations.context.DefaultPublishingContext;
import org.ytoh.configurations.context.PublishingContext;
import org.ytoh.configurations.ui.*;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.bootstrap.GenericBootstrap;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

/**
 * Utility class able to retrieve component properties.
 * <p/>
 * <p>It looks for fields annotation with special annotations that mark object
 * fields as properties. This implementations caches the object properties.</p>
 *
 * @author ytoh
 * @see PropertyExtractor#propertiesFor(java.lang.Object)
 */
public class AnnotationPropertyExtractor implements PropertyExtractor {

    static final Logger logger = Logger.getLogger(AnnotationPropertyExtractor.class);
    /**
     * inernal cache
     */
    private static final Map<Object, List<Property>> cache;
    /**
     * type-specific default PropertyEditor repository
     */
    private final Map<Class<?>, Class<? extends PropertyEditor>> defaultEditors;
    /**
     * type-specific default PropertyRenderer repository
     */
    private final Map<Class<?>, Class<? extends PropertyRenderer>> defaultRenderers;

    ContextAwareConstraintValidatorFactory constraintValidatorFactory = new ContextAwareConstraintValidatorFactory(new ConstraintValidatorFactoryImpl());

    /**
     * validator to validate input values
     */
    private final Validator validator;
    /**
     * context for dynamic property editation
     */
    private final PublishingContext context;
    /**
     * flag signaling if changes should be sandboxed or not
     */
    private final boolean shouldSandbox;

    static {
        cache = new HashMap<Object, List<Property>>();
    }

    /**
     * Creates a default <code>AnnotationPropertyExtractor</code> instance
     * with an empty dynamic context.
     */
    public AnnotationPropertyExtractor() {
        this(new DefaultPublishingContext(new DefaultContext()), false);
    }

    public AnnotationPropertyExtractor(PublishingContext context) {
        this(context, false);
    }

    /**
     * Creates a <code>AnnotationPropertyExtractor</code> instance with
     * the supplied dynamic context.
     *
     * @param context {@link Context} to be used with this <code>PropertyExtractor</code>
     */
    public AnnotationPropertyExtractor(PublishingContext context, boolean shouldSandbox) {
        this.context = context;
        this.shouldSandbox = shouldSandbox;
        this.constraintValidatorFactory.registerContext(context);

        GenericBootstrap byDefaultProvider = Validation.byDefaultProvider();
        Configuration<?> configuration = ((Configuration<?>) byDefaultProvider.configure()).constraintValidatorFactory(constraintValidatorFactory);
        this.validator = configuration.buildValidatorFactory().getValidator();

        Map<Class<?>, Class<? extends PropertyEditor>> editors = new HashMap<Class<?>, Class<? extends PropertyEditor>>();
        editors.put(Boolean.class, CheckBoxEditor.class);
        editors.put(Boolean.TYPE, CheckBoxEditor.class);
        editors.put(String.class, TextFieldEditor.class);
        editors.put(Integer.TYPE, IntegerTextFieldEditor.class);
        editors.put(Double.TYPE, DoubleTextFieldEditor.class);
        defaultEditors = Collections.unmodifiableMap(editors);
        Map<Class<?>, Class<? extends PropertyRenderer>> renderers = new HashMap<Class<?>, Class<? extends PropertyRenderer>>();
        renderers.put(Double.TYPE, DoubleLabel.class);
        defaultRenderers = Collections.unmodifiableMap(renderers);
    }

    /**
     * <p>Extracts fields annotated with {@link org.ytoh.configurations.annotations.Property}
     * and their defined {@link Editor}s and {@link Renderer}s into instances of
     * {@link Property}.</p>
     *
     * @param o object to extract properties from
     * @return a list of <code>Property</code> instances representing properties
     *         defined on the supplied object
     * @throws ConfigurationException if more then one <code>Editor</code> or
     *                                <code>Renderer</code> is defined for any property or if a problem was
     *                                encountered during their instantiation.
     */
    public List<Property> propertiesFor(Object o) {
        try {
            List<Property> cached = cache.get(o);

            return cached != null ? cached : extractProperty(o);
        } catch (InvocationTargetException ex) {
            throw new ConfigurationException("Could not extract properties. ", ex);
        } catch (NoSuchMethodException ex) {
            throw new ConfigurationException("Could not extract properties.", ex);
        } catch (InstantiationException ex) {
            throw new ConfigurationException("Could not extract properties.", ex);
        } catch (IllegalAccessException ex) {
            throw new ConfigurationException("Could not extract properties.", ex);
        }
    }

    /**
     * Extracts declared fields from the given class while minding class inheritance.
     *
     * @param c class to extract fields from
     * @return a list of fields
     */
    private List<Field> extractDeclaredFields(Class c) {
        List<Field> fields = new ArrayList<Field>();

        while (!Object.class.equals(c)) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }

        return fields;
    }

    /**
     * @param o object to extract properties from
     * @return a list of properties for the supplied object
     * @throws java.lang.InstantiationException
     *          in case of problems instantiating property editors or renderers
     * @throws java.lang.IllegalAccessException
     *          in case of problems instantiating property editors or renderers
     * @see PropertyExtractor#propertiesFor(java.lang.Object)
     */
    private List<Property> extractProperty(final Object o) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object sandbox = shouldSandbox ? o.getClass().newInstance() : o;
        Sandbox sandboxContext = Sandbox.newInstance(sandbox);
        // properties extraction
        List<Field> fields = extractDeclaredFields(o.getClass());
        List<MutableProperty> properties = new ArrayList<MutableProperty>();
        for (Field field : fields) {
            if (isProperty(field)) {
                MutableProperty property = null;

                if (isComponentProperty(field.getType())) {
                    property = new DefaultComponentProperty(field.getAnnotation(org.ytoh.configurations.annotations.Property.class), field, sandbox, validator);
                } else if (isArrayProperty(field.getType())) {
                    property = new DefaultArrayProperty(field.getAnnotation(org.ytoh.configurations.annotations.Property.class), field, sandbox, validator);
                } else if (isListProperty(field.getType())) {
                    property = new DefaultListProperty(field.getAnnotation(org.ytoh.configurations.annotations.Property.class), field, sandbox, validator);
                } else if (isMappedProperty(field.getType())) {
                    property = new DefaultMapProperty(field.getAnnotation(org.ytoh.configurations.annotations.Property.class), field, sandbox, validator);
                } else {
                    property = new DefaultProperty(field.getAnnotation(org.ytoh.configurations.annotations.Property.class), field, sandbox, validator);
                }

                Class fieldType = property.getFieldType();
                Annotation editor = getPropertyEditor(field);
                Annotation renderer = getPropertyRenderer(field);

                PropertyEditor<Object, Annotation> propertyEditor = (editor == null ? getDefaultEditor(fieldType) : editor.annotationType().getAnnotation(Editor.class).component().newInstance());
                PropertyRenderer<Object, Annotation> propertyRenderer = (renderer == null ? getDefaultRenderer(fieldType) : renderer.annotationType().getAnnotation(Renderer.class).component().newInstance());

                property.setEditor(propertyEditor, editor, this.context);

                // property renderers
                property.setRenderer(propertyRenderer, renderer);

                // set sandbox to mirror the original
                if (shouldSandbox) {
                    PropertyUtils.setProperty(sandbox, property.getFieldName(), PropertyUtils.getProperty(o, property.getFieldName()));
                }

                property.setValue(PropertyUtils.getProperty(sandbox, property.getFieldName()));

                String fieldName = property.getFieldName();
                // workaround: PropertyUtils does not property detect the state property for single letter properties
                if (fieldName.length() == 1) {
                    fieldName = fieldName.toUpperCase();
                }
                if (PropertyUtils.isReadable(sandbox, fieldName + "State")) {
                    property.setPropertyState((PropertyState) PropertyUtils.getProperty(sandbox, fieldName + "State"));
                }

                // add the property to a specific context
                // hack: for now it is an Abstract property
                sandboxContext.addProperty((AbstractProperty) property);
                properties.add(property);
            }
        }

        return new ArrayList<Property>(properties);
    }

    /**
     * <p>Instantiates and returns a default {@link Component} used to edit
     * the given property based on the type of the underlying field.</p>
     * <p>If no type-specific {@link PropertyEditor} is registered as
     * the default editor specified a NullObject <code>PropertyEditor</code>
     * is used.</p>
     *
     * @param type of the underlying field
     * @return an instance of a <code>Component</code> to be used to edit
     *         the supplied property
     * @throws IllegalAccessException if the default editor component cannot be instantiated.
     * @throws InstantiationException if the default editor component cannot be instantiated.
     * @see PropertyExtractor #DEFAULT_EDITORS
     */
    private <T, A extends Annotation> PropertyEditor<T, A> getDefaultEditor(Class<?> type) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class<? extends PropertyEditor> editor = defaultEditors.get(type);

        if (editor == null) {
            if (isComponentProperty(type)) {
                return (PropertyEditor<T, A>) new PropertyTableEditor();
            } else if (type.isEnum()) {
                return (PropertyEditor<T, A>) new DropDownEditor(type.getEnumConstants());
            }
        } else {
            return editor.newInstance();
        }

        // NullObject design pattern
        return new DefaultEditor();
    }

    /**
     * <p>Instantiates and returns a default {@link Component} used to render
     * the given property based on the type of the underlying field.</p>
     * <p>If no type-specific {@link PropertyRenderer} is registered as
     * the default renderer specified a NullObject <code>PropertyRenderer</code>
     * is used.</p>
     *
     * @param type of the underlying field
     * @return an instance of a <code>Component</code> to be used to render
     *         the supplied property
     * @throws IllegalAccessException if the default renderer component cannot be instantiated.
     * @throws InstantiationException if the default renderer component cannot be instantiated.
     * @see PropertyExtractor #DEFAULT_RENDERERS
     */
    private PropertyRenderer getDefaultRenderer(Class<?> type) throws InstantiationException, IllegalAccessException {
        Class<? extends PropertyRenderer> renderer = defaultRenderers.get(type);

        // NullObject design pattern
        return renderer != null ? renderer.newInstance() : new DefaultRenderer();
    }

    /**
     * <p>Indicates whether or not the given field is a property.</p>
     *
     * @param field
     * @return <code>true</code> if the field is a property
     *         (annotated with {@link org.ytoh.configurations.annotations.Property})
     */
    public boolean isProperty(Field field) {
        return CollectionUtils.exists(Arrays.asList(field.getDeclaredAnnotations()), new Predicate() {

            public boolean evaluate(Object o) {
                return ((Annotation) o) instanceof org.ytoh.configurations.annotations.Property;
            }
        });
    }

    /**
     * <p>Retrieves custom editor annotations (if any) for the given
     * <code>field</code>.</p>
     * <p>Properties can have at most one {@link PropertyEditor}.</p>
     *
     * @param field to analyze
     * @return custom editor annotation
     * @throws ConfigurationException if the suppied field is annotated with
     *                                more then one custom editor annotations
     */
    private Annotation getPropertyEditor(Field field) {
        List<Annotation> editors = Annotations.like(Editor.class, field);
        if (editors.size() > 1) {
            throw new ConfigurationException("Properties can have only one editor (found " + editors.size() + ": " + editors + ") for field: " + field.getName());
        }

        return editors.isEmpty() ? null : editors.get(0);
    }

    /**
     * <p>Retrieves custom renderer annotations (if any) for the given
     * <code>field</code>.</p>
     * <p>Properties can have at most one {@link PropertyRenderer}.</p>
     *
     * @param field to analyze
     * @return custom renderer annotation
     * @throws ConfigurationException if the suppied field is annotated with
     *                                more then one custom renderer annotations
     */
    private Annotation getPropertyRenderer(Field field) {
        List<Annotation> renderers = Annotations.like(Renderer.class, field);
        if (renderers.size() > 1) {
            throw new ConfigurationException("Properties can have only one renderer (found " + renderers.size() + ": " + renderers + ") for field: " + field.getName());
        }

        return renderers.isEmpty() ? null : renderers.get(0);
    }

    /**
     * Checks if this <code>Class</code> type is a list.
     *
     * @param type <code>Class</code> type to check
     * @return <code>true</code> if it is a list, <code>false</code> otherwise
     */
    private boolean isListProperty(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    /**
     * Checks if this <code>Class</code> type is an array.
     *
     * @param type <code>Class</code> type to check
     * @return <code>true</code> if it is an array, <code>false</code> otherwise
     */
    private boolean isArrayProperty(Class<?> type) {
        return type.isArray();
    }

    /**
     * Checks if this <code>Class</code> type is a map.
     *
     * @param type <code>Class</code> type to check
     * @return <code>true</code> if it is a map, <code>false</code> otherwise
     */
    private boolean isMappedProperty(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    /**
     * Checks if this <code>Class</code> type is a {@link org.ytoh.configurations.annotations.Component}.
     *
     * @param type <code>Class</code> type to check
     * @return <code>true</code> if it is a Component, <code>false</code> otherwise
     */
    private boolean isComponentProperty(Class<?> type) {
        return type.isAnnotationPresent(org.ytoh.configurations.annotations.Component.class);
    }
}
