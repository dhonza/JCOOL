package org.ytoh.configurations.test;

import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.FileDirectoryPicker;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.SelectionSet;
import org.ytoh.configurations.ui.SelectionSetModel;

/**
 * Created by IntelliJ IDEA.
 * User: lagon
 * Date: Sep 30, 2009
 * Time: 4:11:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Component(name = "Test configuration", description = "Testing bean", shortDescription = "Testing")
public class Bean4 {


    public enum SelectorChoiceEnum {
        TournamentSelectionEnum, SequentialSelectionEnum
    }


    @Property(name = "How to select individuals for crossover", description = "How to select individuals for crossover")
    private SelectorChoiceEnum selectorChoice = SelectorChoiceEnum.TournamentSelectionEnum;

    @Property(name = "Choose from a set", description = "Choose from a set")
    private String stringValue;

    @Property(name = "Get file", description = "Select file")
    @FileDirectoryPicker(value = "", allowDirectories = false, allowFiles = true, pathMustExist = true, multipleFilesAllowed = false, title = "Select file")
    private String path = "";

    String[] sArr = {"jedna", "dve", "tri"};

    @Property(name = "Selection set", description = "Selection set")
    @SelectionSet(key = "local_methods", type = String.class)
//    private String model = "Ahoj";
//    @Table
//    private java.util.List<String> list;
    private SelectionSetModel<String> model = new SelectionSetModel<String>(sArr);

    private static transient Bean4 self = null;


    private Bean4() {
        super();
        model = new SelectionSetModel<String>();
        model.setElements(sArr);
/*        list = new ArrayList<String>();
        list.add("jedna");
        list.add("dve");*/
    }

    public static Bean4 getInstance() {
        if (self == null) {
            self = new Bean4();
        }
        return self;
    }

    public SelectorChoiceEnum getSelectorChoice() {
        return selectorChoice;
    }

    public void setSelectorChoice(SelectorChoiceEnum selectorChoice) {
        this.selectorChoice = selectorChoice;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SelectionSetModel<String> getModel() {
        return model;
    }

    public void setModel(SelectionSetModel<String> model) {
        this.model = model;
    }

/*    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    } */

/*    public java.util.List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }*/
}
