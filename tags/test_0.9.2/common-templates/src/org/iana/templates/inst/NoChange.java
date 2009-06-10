package org.iana.templates.inst;

/**
 * @author Jakub Laszkiewicz
 */
public class NoChange extends Modificator implements Cloneable {
    private static NoChange instance;

    private NoChange() {
    }

    public static NoChange getInstance() {
        if (instance == null) {
            instance = new NoChange();
        }
        return instance;
    }

    public boolean isNoChange() {
        return true;
    }

    public String toString() {
        return "[no change]";
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
