package org.iana.rzm.trans.change;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
@Table(name = "AChange")
abstract public class Change {

    protected enum Type { ADD, REMOVE, UPDATE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private String fieldName;
    @Enumerated
    private Type type;

    protected Change() {}

    protected Change(String fieldName, Type type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Type getType() {
        return type;
    }

}
