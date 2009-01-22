package org.iana.templates.inst;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class Modificator implements Serializable, Cloneable {
    private static long serialVersionUID = 1L;

    public boolean isRemove() {
        return false;
    }

    public boolean isNoChange() {
        return false;
    }

    public boolean isReplace() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
