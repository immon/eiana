package org.iana.templates.inst;

/**
 * @author Jakub Laszkiewicz
 */
public class Replace extends Modificator implements Cloneable {
    private String oldElementName;

    public Replace(String oldElementName) {
        this.oldElementName = oldElementName;
    }

    public String getOldElementName() {
        return oldElementName;
    }

    public boolean isReplace() {
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[replace ").append(oldElementName).append("]");
        return sb.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        Replace cloned = (Replace) super.clone();
        cloned.oldElementName = oldElementName;
        return cloned;
    }
}
