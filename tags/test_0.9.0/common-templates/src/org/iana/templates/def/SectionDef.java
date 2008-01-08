package org.iana.templates.def;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public class SectionDef extends ElementDef implements Serializable {
    private static long serialVersionUID = 1L;

    private List<TemplateObjectDef> elements;
    private Map<String, TemplateObjectDef> map;

    public SectionDef() {
    }

    public SectionDef(String name, String label) {
        this(name, label, null, true);
    }

    public SectionDef(String name, String label, List<TemplateObjectDef> elements, boolean required) {
        super(name, label, required);
        if (elements == null) {
            this.elements = new ArrayList<TemplateObjectDef>();
            this.map = new HashMap<String, TemplateObjectDef>();
        } else {
            this.elements = elements;
            this.map = createMap(elements);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        SectionDef cloned = (SectionDef) super.clone();
        cloned.elements = new ArrayList<TemplateObjectDef>();
        for (TemplateObjectDef element : elements)
            cloned.addTemplateObjectDef((TemplateObjectDef) element.clone());
        return cloned;
    }

    public TemplateObjectDef getElementDefByName(String name) {
        return map.get(name);
    }

    private Map<String, TemplateObjectDef> createMap(List elements) {
        Map<String, TemplateObjectDef> newMap = new HashMap<String, TemplateObjectDef>();
        for (Object element : elements) {
            TemplateObjectDef tod = (TemplateObjectDef) element;
            newMap.put(tod.getName(), tod);
        }
        return newMap;
    }

    public TemplateObjectDef get(String name) {
        return map.get(name);
    }

    public List<TemplateObjectDef> getElementDefs() {
        return elements;
    }

    public Map<String, TemplateObjectDef> getElementDefsMap() {
        return map;
    }

    public void addTemplateObjectDef(TemplateObjectDef tod) {
        elements.add(tod);
        map.put(tod.getName(), tod);
    }

    public void accept(TemplateObjectDefVisitor v) {
        v.visitSectionDef(this);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("section ");
        sb.append(getName());
        sb.append(" \"");
        sb.append(getLabel());
        sb.append("\" (");
        for (TemplateObjectDef element : elements) {
            sb.append("\n");
            sb.append(element);
        }
        sb.append("\n)");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        return obj instanceof SectionDef && super.equals(obj);
    }
}
