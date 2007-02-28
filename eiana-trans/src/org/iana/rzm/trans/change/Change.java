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

    private Long objId;
    private String fieldName;
    private Type type;

    protected Change() {}

    protected Change(String fieldName, Type type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getFieldName() {
        return fieldName;
    }

    private void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Type getType() {
        return type;
    }

    private void setType(Type type) {
        this.type = type;
    }

}
