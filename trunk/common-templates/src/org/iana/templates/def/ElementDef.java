package org.iana.templates.def;

/**
 * @author Jakub Laszkiewicz
 */
public abstract class ElementDef extends TemplateObjectDef implements Cloneable {
    private String name;
    private String label;
    private boolean required;

    protected ElementDef() {
    }

    protected ElementDef(String name, String label, boolean required) {
        this.name = name;
        this.label = label;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ElementDef) {
            ElementDef ed = (ElementDef) obj;
            return name == null ? ed.name == null : name.equals(ed.name);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        ElementDef cloned = (ElementDef) super.clone();
        cloned.label = label;
        cloned.name = name;
        cloned.required = required;
        return cloned;
    }
}
