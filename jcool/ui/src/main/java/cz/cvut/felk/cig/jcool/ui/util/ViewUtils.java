/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.util;

import org.ytoh.configurations.MutableProperty;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.context.MutableContext;
import org.ytoh.configurations.ui.DynamicDropDown;
import org.ytoh.configurations.util.PropertyExtractor;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ytoh
 */
public class ViewUtils {

    public static void centerOnComponent(Component toCenter, Component toCenterOn) {
        Dimension d = toCenterOn.getSize();
        Point p = toCenterOn.getLocation();
        toCenter.setLocation(p.x + d.width / 2 - (toCenter.getPreferredSize().width / 2), p.y + d.height / 2 - (toCenter.getPreferredSize().height / 2));
    }

    public static void centerOnScreen(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        component.setLocation(screenSize.width / 2 - (component.getPreferredSize().width / 2),
                screenSize.height / 2 - (component.getPreferredSize().height / 2));
    }

    public static void fullScreen(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        component.setSize(screenSize);
        component.setLocation(0,0);
    }

    public static void toTopRightCorner(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        component.setLocation(screenSize.width - (component.getPreferredSize().width), 0);
    }

    /**
     * Returns a List of all underlying Components of given object.
     * @param component - Components which sub-components are subject of interest.
     * @param extractor - extractor that knows what annotation to look for.
     * @return List<Object> of found components.
     */
//    public static List<Object> getComponents(Object component, PropertyExtractor extractor){
//        List<Object> ret = new ArrayList<Object>();
//        if (component != null) {
//            List<org.ytoh.configurations.Property> properties = extractor.propertiesFor(component);
//            for (org.ytoh.configurations.Property property : properties){
//                if (property.getValue() != null){
//                    if (property.getValue().getClass().getAnnotation(org.ytoh.configurations.annotations.Component.class) != null){
//                        // add components and extract its sub-components if any
//                        ret.add(property.getValue()); // add the component
//                        ret.addAll(getComponents(property.getValue(), extractor));
//                    }
//                }
//            }
//        }
//        return ret;
//    }

    /**
     * Returns a List of all underlying Components of given object.
     * If explored component is DynamicDropDown, then if current value is not present in list of current context, then null object reference is set and component is not recursively processed..
     * @param component - Components which sub-components are subject of interest.
     * @param extractor - extractor that knows what annotation to look for.
     * @param context - current context in which the DynamicDropDown is being generated.
     * @param removeIfNotInContext - flag indicating whether remove from processing components that are declared through DynamicDropDown but are not present in current context list.
     * @return List<Object> of found components.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> getComponents(Object component, PropertyExtractor extractor, MutableContext context, boolean removeIfNotInContext){
        List<Object> ret = new ArrayList<Object>();
        if (component != null) {
            List<Field> dynamicFields = (removeIfNotInContext) ? getDynamicPropertyFields(component) : null;
            List<org.ytoh.configurations.Property> properties = extractor.propertiesFor(component);
            for (org.ytoh.configurations.Property property : properties){
                if (property.getValue() != null){
                    if (property.getValue().getClass().getAnnotation(org.ytoh.configurations.annotations.Component.class) != null){
                        DynamicDropDown dropDown;
                        // do we want to remove it if it is DynamicDropDown component with value outside defined context?
                        if (removeIfNotInContext && (dropDown = getDynamicDropDownAnnotation(((MutableProperty)property).getFieldName(), dynamicFields)) != null){
                            List<?> allowedComponents = context.getList(dropDown.type(), dropDown.key());
                            if (allowedComponents != null && propertyIsInList(property, allowedComponents)){
                                // add components and extract its sub-components if any
                                ret.add(property.getValue()); // add the component
                                ret.addAll(getComponents(property.getValue(), extractor, context, removeIfNotInContext));
                            } else {
                                // else property is outside context and will be blanked
                                property.setValue(null); // unchecked warning
                            }
                        } else {
                            // add components and extract its sub-components if any
                            ret.add(property.getValue()); // add the component
                            ret.addAll(getComponents(property.getValue(), extractor, context, removeIfNotInContext));
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Returns true if property is in given list. Presence is determined not only on reference comparison but on Class object as well.
     * @param property - property which value is being inspected.
     * @param objects - list of objects in which the property should be.
     * @return true if property is in given List.
     */
    @SuppressWarnings("unchecked")
    public static boolean propertyIsInList(org.ytoh.configurations.Property property, List<?> objects){
        Class propertyClass = property.getValue().getClass();
        for (Object object : objects){
            if (object.getClass().equals(propertyClass)){
                if (property.getValue() != object){
                    property.setValue(object); // unchecked warning
                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param fieldName
     * @param dynamicPropertyFields
     * @return
     */
    public static DynamicDropDown getDynamicDropDownAnnotation(String fieldName, List<Field> dynamicPropertyFields){
        for (Field field : dynamicPropertyFields){
            if (field.getName().equals(fieldName)){
                return field.getAnnotation(DynamicDropDown.class);
            }
        }
        return null;
    }

    /**
     * Inspects given object and extracts its fields with both @Property and @DynamicDropDown annotations.
     * @param object - object to be inspected.
     * @return List<Field> of fields with @Property and @DynamicDropDown annotations.
     */
    public static List<Field> getDynamicPropertyFields(Object object){
        List<Field> fieldList = new ArrayList<Field>();
        Class cls = object.getClass();

        // recursive extraction of fields
        while (!Object.class.equals(cls)){
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields){
                if ( (field.getAnnotation(Property.class) != null) && (field.getAnnotation(DynamicDropDown.class) != null) ){
                    fieldList.add(field);
                }
            }
            cls = cls.getSuperclass();
        }
        return fieldList;
    }
}
