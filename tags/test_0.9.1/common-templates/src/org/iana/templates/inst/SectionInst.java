package org.iana.templates.inst;

import org.iana.templates.def.SectionDef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public class SectionInst extends ElementInst implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<TemplateObjectInst> instances = new ArrayList<TemplateObjectInst>();
    private Map<String, TemplateObjectInst> map = new HashMap<String, TemplateObjectInst>();

    public SectionInst() {
    }

    public SectionInst(SectionDef definition, String name) {
        this(definition, name, null);
    }

    public SectionInst(SectionDef definition, String name, List<TemplateObjectInst> instances) {
        super(definition);
        if (instances == null) {
            this.instances = new ArrayList<TemplateObjectInst>();
            map = new HashMap<String, TemplateObjectInst>();
        } else {
            this.instances = instances;
            map = createMap(instances);
        }
    }

    private Map<String, TemplateObjectInst> createMap(List<TemplateObjectInst> instances) {
        Map<String, TemplateObjectInst> newMap = new HashMap<String, TemplateObjectInst>();
        for (TemplateObjectInst instance : instances)
            newMap.put(instance.getName(), instance);
        return newMap;
    }

    public void accept(TemplateObjectInstVisitor v) {
        v.visitSectionInst(this);
    }

    public Object clone() throws CloneNotSupportedException {
        SectionInst cloned = (SectionInst) super.clone();
        for (TemplateObjectInst instance : instances)
            cloned.addInstance((TemplateObjectInst) instance.clone());
        return cloned;
    }

    public void untype() {
        super.untype();
        for (TemplateObjectInst instance : instances)
            instance.untype();
    }

    public boolean isEmpty() {
        return instances == null || instances.isEmpty() || isRemoved();
    }

    public List<TemplateObjectInst> getInstances() {
        return instances;
    }

    public Map<String, TemplateObjectInst> getInstancesMap() {
        return map;
    }

    public void setInstances(List<TemplateObjectInst> instances) {
        this.instances = instances;
        this.map = createMap(instances);
    }

    public void addInstance(TemplateObjectInst instance) {
        this.instances.add(instance);
        map.put(instance.getName(), instance);
    }

    public TemplateObjectInst getInstance(String name) {
        return map.get(name);
    }

    public SectionInst getSectionInstance(String name) {
        TemplateObjectInst inst = getInstance(name);
        return (inst instanceof SectionInst) ? (SectionInst) inst : null;
    }

    public ListInst getListInstance(String name) {
        TemplateObjectInst inst = getInstance(name);
        return (inst instanceof ListInst) ? (ListInst) inst : null;
    }

    public FieldInst getFieldInstance(String name) {
        TemplateObjectInst inst = getInstance(name);
        return (inst instanceof FieldInst) ? (FieldInst) inst : null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getLabel());
        if (getModificator() != null) {
            sb.append(" ");
            sb.append(getModificator());
        }
        for (TemplateObjectInst instance : instances) {
            sb.append("\n");
            sb.append(instance);
        }
        return sb.toString();
    }
}
