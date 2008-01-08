package org.iana.templates.def;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class FieldDef extends ElementDef implements Serializable {
    private static long serialVersionUID = 1L;

    private String regex;

    public FieldDef() {
    }

    public FieldDef(String name, String label) {
        this(name, label, true);
    }

    public FieldDef(String name, String label, boolean required) {
        super(name, label, required);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Object clone() throws CloneNotSupportedException {
        FieldDef cloned = (FieldDef) super.clone();
        cloned.regex = regex;
        return cloned;
    }

    public void accept(TemplateObjectDefVisitor v) {
        v.visitFieldDef(this);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("field ");
        sb.append(getName());
        sb.append(" \"");
        sb.append(getLabel());
        sb.append("\" \"");
        sb.append(regex);
        sb.append("\" ");
        if (isRequired()) {
            sb.append("required ");
        } else {
            sb.append("optional ");
        }
        return sb.toString();
    }

    public boolean isAtom() {
        return true;
    }

    public boolean equals(Object obj) {
        return obj instanceof FieldDef && super.equals(obj);
    }
}
