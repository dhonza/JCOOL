package org.ytoh.configurations.test;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.ytoh.configurations.annotations.Component;
import org.ytoh.configurations.annotations.Property;
import org.ytoh.configurations.annotations.Range;

/**
 *
 * @author ytoh
 */
@Component(name="Test bean", description="A subroutine")
public class Bean2 {

    @Property(name="Be fast")
    @AssertTrue
    private boolean beFast = true;

    @Property(name="Act slow")
    private boolean beSlow;

    @Property(name="Enum test")
    @NotNull
    private Type type = Type.TYPE_2;

    @Property(name="String property")
    @NotEmpty
    @NotNull
    @MaxLength(4)
    @Pattern(regexp="[0-9]+")
    private String s = "1234";

    @Property(name="Counter")
    @Range(from=10, to=500)
    private int c = 10;

    @Property(name="Double d ;-)")
    private double d = 0.0;

    @Property(name="Third level bean")
    private Bean3 bean = new Bean3();

    public Bean3 getBean() {
        return bean;
    }

    public void setBean(Bean3 bean) {
        this.bean = bean;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean getBeSlow() {
        return beSlow;
    }

    public void setBeSlow(boolean beSlow) {
        this.beSlow = beSlow;
    }

    public boolean getBeFast() {
        return beFast;
    }

    public void setBeFast(boolean beFast) {
        this.beFast = beFast;
    }

    public static enum Type {
        TYPE_1, TYPE_2, TYPE_3
    }
}
