package org.iana.objectdiff;

import javax.persistence.*;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class ObjectChange extends Change {

    @Basic
    private String id;
    @Basic
    private String clazz;
    @OneToMany(cascade = CascadeType.ALL, targetEntity = FieldChange.class)
    @JoinTable(name = "CollectionChange_FieldChanges",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
    private Collection<FieldChange> fieldChangeList = new ArrayList<FieldChange>();
    @Transient
    private Map<String, Change> fieldChanges = new HashMap<String, Change>();

    private ObjectChange() {}

    public ObjectChange(Type type, String id, Map<String, Change> fieldChanges) {
        super(type);
        this.id = id;
        for (Map.Entry<String, Change> entry : fieldChanges.entrySet()) {
            fieldChangeList.add(new FieldChange(entry.getKey(), entry.getValue()));
        }
        this.fieldChanges = fieldChanges;
    }

    public String getId() {
        return id;
    }

    public Map<String, Change> getFieldChanges() {
        if (fieldChanges.isEmpty()) {
            for (FieldChange fc : fieldChangeList) {
                fieldChanges.put(fc.fieldName, fc.change);
            }
        }
        return fieldChanges;
    }

    public void accept(ChangeVisitor v) {
        v.visitObject(this);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectChange that = (ObjectChange) o;

        if (fieldChangeList != null ? !fieldChangeList.equals(that.fieldChangeList) : that.fieldChangeList != null)
            return false;

        return true;
    }

    public int hashCode() {
        return (fieldChangeList != null ? fieldChangeList.hashCode() : 0);
    }
}
