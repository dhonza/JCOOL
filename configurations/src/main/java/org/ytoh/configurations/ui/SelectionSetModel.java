package org.ytoh.configurations.ui;

import com.jgoodies.binding.beans.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Oct 10, 2009
 * Time: 9:48:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectionSetModel<T> extends Model {
    private T [] elements;
    private boolean [] elementEnabled;

    public SelectionSetModel() {
        elements = null;
        elementEnabled = new boolean[0];
    }

    public SelectionSetModel(T [] elements) {
        this.elements = elements.clone();
        elementEnabled = new boolean[elements.length];
        enableAllElements();
    }

    public void setElements(T [] elements) {
        this.elements = elements.clone();
        elementEnabled = new boolean[elements.length];
        enableAllElements();
    }

    public void enableAllElements() {
        for (int i = 0; i < elements.length; i++) {
            elementEnabled[i] = true;
        }
        firePropertyChange("Enabled",false,true);
    }

    public void disableAllElements() {
        for (int i = 0; i < elements.length; i++) {
            elementEnabled[i] = false;
        }
        firePropertyChange("Enabled",true,false);
    }

    public void enableElementIndices(int [] indices) {
        disableAllElements();
        for (int i = 0; i < indices.length; i++) {
            elementEnabled[indices[i]] = true; 
        }
    }

    public int [] getEnableElementIndices() {
        ArrayList<Integer> enabledIndices = new ArrayList<Integer>(elements.length);
        for (int i = 0; i < elements.length; i++) {
            if (elementEnabled[i]) {
                enabledIndices.add(i);
            }
        }
        int [] list = new int[enabledIndices.size()];
        for (int i = 0; i < list.length; i++) {
            list[i] = enabledIndices.get(i);
        }
        return list;
    }

    public void enableElement(int idx) {
        setElementState(idx, true);
    }

    public void disableElement(int idx) {
        setElementState(idx, false);
    }

    private void setElementState(int idx, boolean state) {
        if (idx > elementEnabled.length) {
            throw new IndexOutOfBoundsException("Index is higher that number of available elements. ( " + idx + " > " + elementEnabled.length + " )");
        }
        boolean oldState = elementEnabled[idx];
        elementEnabled[idx] = state;
        fireIndexedPropertyChange("Enabled", idx, oldState, state);
    }

    public boolean [] getStateOfElements() {
        return elementEnabled.clone();
    }

    public T[] getAllElements() {
        return elements.clone();
    }

    public T[] getEnabledElements(Class<T> type) {
        int count = getCountOfElementsWithState(true);
        T [] elementsToReturn = (T[]) Array.newInstance(type, count);
        int retIndex = 0;
        for (int i = 0; i < elementEnabled.length; i++) {
            if (elementEnabled[i]) {
                elementsToReturn[retIndex] = elements[i];
                retIndex++;
            }
        }
        return elementsToReturn;
    }

    public T[] getDisabledElements(Class<T> type) {
        int count = getCountOfElementsWithState(false);
        T [] elementsToReturn = (T[]) Array.newInstance(type, count);
        int retIndex = 0;
        for (int i = 0; i < elementEnabled.length; i++) {
            if (!elementEnabled[i]) {
                elementsToReturn[retIndex] = elements[i];
                retIndex++;
            }
        }
        return elementsToReturn;
    }

    private int getCountOfElementsWithState(boolean state) {
        int count = 0;
        for (int i = 0; i < elementEnabled.length; i++) {
            if (elementEnabled[i] == state) {
                count++;
            }
        }
        return count;
    }

    public static SelectionSetModel assembleModel(Object [] elements, int [] enabledIndices) {
        SelectionSetModel model = new SelectionSetModel(elements);
        model.enableElementIndices(enabledIndices);
        return model;
    }

}
