package org.iana.rzm.trans.change;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ObjectA {
    String name;
    String string1;
    Boolean boolean1;

    public ObjectA() {
    }

    public ObjectA(String name, String string1, Boolean boolean1) {
        this.name = name;
        this.string1 = string1;
        this.boolean1 = boolean1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public Boolean getBoolean1() {
        return boolean1;
    }

    public void setBoolean1(Boolean boolean1) {
        this.boolean1 = boolean1;
    }

    public void setBoolean1(String boolean1) {
        this.boolean1 = boolean1 == null ? null : Boolean.valueOf(boolean1);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectA objectA = (ObjectA) o;

        if (boolean1 != null ? !boolean1.equals(objectA.boolean1) : objectA.boolean1 != null) return false;
        if (name != null ? !name.equals(objectA.name) : objectA.name != null) return false;
        if (string1 != null ? !string1.equals(objectA.string1) : objectA.string1 != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (string1 != null ? string1.hashCode() : 0);
        result = 31 * result + (boolean1 != null ? boolean1.hashCode() : 0);
        return result;
    }
}
