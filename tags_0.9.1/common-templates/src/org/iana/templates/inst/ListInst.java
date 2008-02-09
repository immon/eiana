package org.iana.templates.inst;

import org.iana.templates.def.ListDef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class ListInst extends TemplateObjectInst implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ElementInst> list = new ArrayList<ElementInst>();

    public ListInst() {
    }

    public ListInst(ListDef definition) {
        this(definition, null);
    }

    public ListInst(ListDef definition, List<ElementInst> list) {
        super(definition);
        this.list = list == null ? new ArrayList<ElementInst>() : list;
    }

    public void accept(TemplateObjectInstVisitor v) {
        v.visitListInst(this);
    }

    public Object clone() throws CloneNotSupportedException {
        ListInst cloned = (ListInst) super.clone();
        for (ElementInst elementInst : list)
            cloned.add((ElementInst) elementInst.clone());
        return cloned;
    }

    public boolean isEmpty() {
        return list == null || list.isEmpty() || isRemoved();
    }

    public boolean isRemoved() {
        for (ElementInst elementInst : list) {
            if (!elementInst.isRemoved())
                return false;
        }
        return true;
    }

    public List<ElementInst> getList() {
        return list;
    }

    public void setList(List<ElementInst> list) {
        this.list = list;
    }

    public int size() {
        return list.size();
    }

    public void add(ElementInst elementInst) {
        list.add(elementInst);
    }

    public void untype() {
        super.untype();
        for (ElementInst elementInst : list)
            elementInst.untype();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator i = list.iterator();
        if (i.hasNext()) {
            sb.append(i.next());
            while (i.hasNext()) {
                sb.append("\n");
                sb.append(i.next());
            }
        }
        return sb.toString();
    }

    public void setModificator(Modificator modificator) {
        for (ElementInst elementInst : list)
            elementInst.setModificator(modificator);
    }
}
