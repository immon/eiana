package org.iana.templates.def;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class ListDef extends TemplateObjectDef implements Serializable {
    private static long serialVersionUID = 1L;

    private ElementDef elementDef;

    public ListDef() {
    }

    public ListDef(ElementDef elementDef) {
        this.elementDef = elementDef;
    }

    public Object clone() throws CloneNotSupportedException {
        ListDef cloned = (ListDef) super.clone();
        if (elementDef != null)
            cloned.elementDef = (ElementDef) elementDef.clone();
        return cloned;
    }

    public ElementDef getElementDef() {
        return elementDef;
    }

    public String getName() {
        return elementDef.getName();
    }

    public String getLabel() {
        return elementDef.getLabel();
    }

    public boolean isRequired() {
        return elementDef.isRequired();
    }

    public void accept(TemplateObjectDefVisitor v) {
        v.visitListDef(this);
    }

    public String toString() {
        return "list (" + elementDef + ")";
    }

    public boolean equals(Object obj) {
        if (obj instanceof ListDef) {
            ListDef ld = (ListDef) obj;
            return elementDef.equals(ld.getElementDef());
        }
        return false;
    }
}
