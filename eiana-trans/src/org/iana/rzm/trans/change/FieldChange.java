package org.iana.rzm.trans.change;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
class FieldChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long objId;
    @Basic
    String fieldName;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Change_objId")
    Change change;

    public FieldChange(String fieldName, Change change) {
        this.fieldName = fieldName;
        this.change = change;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldChange that = (FieldChange) o;

        if (change != null ? !change.equals(that.change) : that.change != null) return false;
        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (fieldName != null ? fieldName.hashCode() : 0);
        result = 31 * result + (change != null ? change.hashCode() : 0);
        return result;
    }
}
