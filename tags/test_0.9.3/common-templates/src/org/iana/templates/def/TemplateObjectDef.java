package org.iana.templates.def;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class TemplateObjectDef implements Serializable, Cloneable {
    private static long serialVersionUID = 1l;

    public abstract void accept(TemplateObjectDefVisitor v);

    public abstract String getName();

    public abstract String getLabel();

    public abstract boolean isRequired();

    public boolean isAtom() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
