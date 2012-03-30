package org.ytoh.configurations.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.hibernate.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.NotEmpty;
import org.ytoh.configurations.PropertyState;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.ui.CheckBox;
import org.ytoh.configurations.annotations.Table;

/**
 *
 * @author ytoh
 */
@Component(name="First bean")
public class Bean {

    @Property(name="ON OFF")
    @MaxLength(20)
    @NotEmpty
    private String s = "ON!";

    @Property(name="switch property", description="switchs the state of the hello world property.")
    @CheckBox
    private boolean b = true;

    @Property
    @Table
    private List<Switcher> switchers = Arrays.asList(new Switcher[] { new Switcher(), new Switcher() });

    @Property(name="Map of booleans")
    @CheckBox
    private Map<String, Boolean> map = new HashMap<String, Boolean>();

    public Bean() {
        map.put("z", false);
        map.put("x", true);
        map.put("y", false);
        array.add(false);
        array.add(false);
        array.add(false);
        array.add(false);
        array.add(false);
    }

    public Map<String, Boolean> getMap() {
        return map;
    }

    public void setMap(Map<String, Boolean> map) {
        this.map = map;
    }

    @Property
    @CheckBox
    private List<Boolean> array = new ArrayList<Boolean>();

    @Property
    @Table
    private Switcher switcher = new Switcher();

    public Switcher getSwitcher() {
        return switcher;
    }

    public void setSwitcher(Switcher switcher) {
        this.switcher = switcher;
    }

    public List<Boolean> getArray() {
        return array;
    }

    public void setArray(List<Boolean> array) {
        this.array = array;
    }

    public PropertyState getSState() {
        return b ? PropertyState.ENABLED : PropertyState.DISABLED;
    }

    public List<Switcher> getSwitchers() {
        return switchers;
    }

    public void setSwitchers(List<Switcher> switchers) {
        this.switchers = switchers;
    }

//    @Property
//    @Range(from = .5, to = 5)
//    private double d = 0.0;
//
//    @Property
//    @NotNull
//    @Pattern(regex = "ONE|TWO|THREE")
//    private String one;

//    @Property(name = "Subroutine")
    private Bean2 bean2 = new Bean2();
//
//    @Property(name = "Subroutine")
//    private Bean2 bean3 = new Bean2();

//    public Bean2 getBean3() {
//        return bean3;
//    }
//
    public void setBean2(Bean2 bean2) {
        this.bean2 = bean2;
    }
//
    public Bean2 getBean2() {
        return bean2;
    }

    public boolean getB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
        this.s = b ? "ON!" : "OFF!";
    }

//    public String getOne() {
//        return one;
//    }
//
//    public void setOne(String one) {
//        this.one = one;
//    }


    public String getS() {
        return s;
    }

    public void setS(String str) {
        this.s = str;
    }

//    public double getD() {
//        return d;
//    }
//
//    public void setD(double d) {
//        this.d = d;
//    }

}
