package org.iana.rzm.trans.change;

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
    private long id;
    @Basic
    @Column(name = "objectValueName")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, targetEntity = Change.class)
    @JoinTable(name = "ObjectValue_Changes",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
    private List<? extends T> changes;

    public ObjectValue(List<? extends T> changes) {
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

    public List<? extends Change> getChanges() {
        return changes;
    }

    public void setChanges(List<? extends T> changes) {
        this.changes = changes;
    }
}
