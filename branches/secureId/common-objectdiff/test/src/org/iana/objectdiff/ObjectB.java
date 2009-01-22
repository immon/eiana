package org.iana.objectdiff;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ObjectB {
    String name;
    String string1;
    String string2;
    ObjectA object;
    Collection<String> strings = new ArrayList<String>();
    Collection<ObjectA> objects = new ArrayList<ObjectA>();

    public ObjectB() {
    }

    public ObjectB(String name, String string1, String string2, ObjectA object) {
        this.name = name;
        this.string1 = string1;
        this.string2 = string2;
        this.object = object;
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

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }

    public ObjectA getObject() {
        return object;
    }

    public void setObject(ObjectA object) {
        this.object = object;
    }

    public Collection<String> getStrings() {
        return strings;
    }

    public void setStrings(Collection<String> strings) {
        this.strings = strings;
    }

    public Collection<ObjectA> getObjects() {
        return objects;
    }

    public void setObjects(Collection<ObjectA> objects) {
        this.objects = objects;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectB objectB = (ObjectB) o;

        if (name != null ? !name.equals(objectB.name) : objectB.name != null) return false;
        if (object != null ? !object.equals(objectB.object) : objectB.object != null) return false;
        if (objects != null ? !objects.equals(objectB.objects) : objectB.objects != null) return false;
        if (string1 != null ? !string1.equals(objectB.string1) : objectB.string1 != null) return false;
        if (string2 != null ? !string2.equals(objectB.string2) : objectB.string2 != null) return false;
        if (strings != null ? !strings.equals(objectB.strings) : objectB.strings != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (string1 != null ? string1.hashCode() : 0);
        result = 31 * result + (string2 != null ? string2.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (strings != null ? strings.hashCode() : 0);
        result = 31 * result + (objects != null ? objects.hashCode() : 0);
        return result;
    }
}
