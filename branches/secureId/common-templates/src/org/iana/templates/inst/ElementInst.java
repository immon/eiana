package org.iana.templates.inst;

import org.iana.templates.def.TemplateObjectDef;


/**
 * @author Jakub Laszkiewicz
 */
public abstract class ElementInst extends TemplateObjectInst implements Cloneable {
    private Modificator modificator;

    protected ElementInst() {
    }

    protected ElementInst(TemplateObjectDef def) {
        super(def);
    }

    public Modificator getModificator() {
        return modificator;
    }

    public void setModificator(Modificator modificator) {
        this.modificator = modificator;
    }

    public boolean isRemoved() {
        return modificator != null && modificator.isRemove();
    }

    public boolean isReplaced() {
        return modificator != null && modificator.isReplace();
    }

    public String getReplacedElementName() {
        if (isReplaced())
            return ((Replace) getModificator()).getOldElementName();
        return null;
    }

    public String getRemovedElementName() {
        if (isRemoved())
            return ((Remove) getModificator()).getElementName();
        return null;
    }

    public boolean isNotChanged() {
        return modificator != null && modificator.isNoChange();
    }

    public Object clone() throws CloneNotSupportedException {
        ElementInst cloned = (ElementInst) super.clone();
        if (modificator != null)
            cloned.modificator = (Modificator) modificator.clone();
        return cloned;
    }
}
