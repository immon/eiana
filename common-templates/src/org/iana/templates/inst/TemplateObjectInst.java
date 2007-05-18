package org.iana.templates.inst;

import org.iana.templates.def.TemplateObjectDef;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class TemplateObjectInst implements Serializable, Cloneable {
    private static long serialVersionUID = 1L;

    private String name;
    private TemplateObjectDef def;

    protected TemplateObjectInst() {
    }

    protected TemplateObjectInst(TemplateObjectDef def) {
        this.def = def;
        this.name = def.getName();
    }

    public TemplateObjectDef getDefinition() {
        return def;
    }

    public Object clone() throws CloneNotSupportedException {
        TemplateObjectInst cloned = (TemplateObjectInst) super.clone();
        cloned.name = name;
        if (def != null)
            cloned.def = (TemplateObjectDef) def.clone();
        return cloned;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return getDefinition().getLabel();
    }

    public void setDefinition(TemplateObjectDef def) {
        if (!name.equals(def.getName()))
            throw new IllegalArgumentException("expected definition " + name + " doesn't match a given one " + def.getName());
        this.def = def;
    }

    public boolean isInstanceOf(TemplateObjectDef def) {
        return getDefinition().equals(def);
    }

    public void untype() {
        def = null;
    }

    public abstract void accept(TemplateObjectInstVisitor v);

    public abstract boolean isEmpty();

}
