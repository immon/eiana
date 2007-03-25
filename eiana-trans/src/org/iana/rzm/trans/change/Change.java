package org.iana.rzm.trans.change;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
@Table(name = "ANewChange")
abstract public class Change {

    public enum Type {ADDITION, REMOVAL, UPDATE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Enumerated
    private Type type;

    protected Change(Type type) {
        CheckTool.checkNull(type, "change type");
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isAddition() {
        return type == Type.ADDITION;
    }

    public boolean isRemoval() {
        return type == Type.REMOVAL;
    }

    public boolean isModification() {
        return type == Type.UPDATE;
    }

    abstract public void accept(ChangeVisitor v);

    public Long getObjId() {
        return objId;
    }
}
