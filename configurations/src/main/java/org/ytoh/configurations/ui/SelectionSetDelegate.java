package org.ytoh.configurations.ui;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Nov 17, 2009
 * Time: 3:09:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectionSetDelegate extends PersistenceDelegate {

    @Override
    protected Expression instantiate(Object oldInstance, Encoder out) {
        SelectionSetModel model = (SelectionSetModel) oldInstance;


        System.out.printf("Running delegate\n");

        Object [] items = model.getAllElements();
        int [] enabledIdcs = model.getEnableElementIndices();

        Expression expElems = new Expression(model, model.getClass(), "assembleModel", new Object [] {items, enabledIdcs});
        return expElems;
    }

}
