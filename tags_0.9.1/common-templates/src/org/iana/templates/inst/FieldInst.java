package org.iana.templates.inst;

import org.iana.templates.def.FieldDef;

import java.io.Serializable;


/**
 * @author Jakub Laszkiewicz
 */
public class FieldInst extends ElementInst implements Serializable {
    private static final long serialVersionUID = 1L;

    private String value;

    public FieldInst() {
    }

    public FieldInst(FieldDef definition, String name) {
        this(definition, name, null);
    }

    public FieldInst(FieldDef definition, String name, String value) {
        super(definition);
        this.value = value;
    }

    public Object clone() throws CloneNotSupportedException {
        FieldInst cloned = (FieldInst) super.clone();
        cloned.value = value;
        return cloned;
    }

    public void accept(TemplateObjectInstVisitor v) {
        v.visitFieldInst(this);
    }

    public void acceptAndPropagate(TemplateObjectInstVisitor v) {
        v.visitFieldInst(this);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getLabel());
        if (value != null) sb.append(": ").append(value);
        if (getModificator() != null) sb.append(" ").append(getModificator());
        return sb.toString();
    }

    public String getValue() {
        return value;
    }

    public String getModifiedValue() {
        return isRemoved() ? null : getValue();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value == null || isRemoved();
    }


}
