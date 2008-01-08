package org.iana.templates.inst;

/**
 * @author Jakub Laszkiewicz
 */
public class Remove extends Modificator implements Cloneable {
    private String elementName;

    public Remove(String elementName) {
        this.elementName = elementName;
    }

    public String getElementName() {
        return elementName;
    }

    public boolean isRemove() {
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[remove ").append(elementName).append("]");
        return sb.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        Remove cloned = (Remove) super.clone();
        cloned.elementName = elementName;
        return cloned;
    }
}
