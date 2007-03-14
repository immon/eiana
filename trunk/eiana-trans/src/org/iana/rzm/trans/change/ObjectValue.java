package org.iana.rzm.trans.change;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class ObjectValue<T extends Change> extends AbstractValue<T> implements Value<T> {

    @Basic
    @Column(name = "objectValueId")
    private Long id;
    @Basic
    @Column(name = "objectValueName")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, targetEntity = Change.class)
    @JoinTable(name = "ObjectValue_Changes",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
    private List<? extends T> changes;

    private ObjectValue() {
    }

    public ObjectValue(List<? extends T> changes) {
        this.changes = changes;
    }

    public ObjectValue(long id, String name, List<? extends T> changes) {
        this.id = id;
        this.name = name;
        this.changes = changes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<? extends T> getChanges() {
        return changes;
    }

    public void setChanges(List<? extends T> changes) {
        this.changes = changes;
    }

    public void accept(ValueVisitor visitor) {
        CheckTool.checkNull(visitor, "value visitor");
        visitor.visitObjectValue(this);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectValue that = (ObjectValue) o;

        if (changes != null ? !changes.equals(that.changes) : that.changes != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (changes != null ? changes.hashCode() : 0);
        return result;
    }
}
