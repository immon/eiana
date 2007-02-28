package org.iana.rzm.trans.change;

import javax.persistence.*;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class ObjectValue<T extends Change> extends AbstractValue<T> implements Value<T> {

    private long id;
    private String name;
    private List<? extends T> changes;

    public ObjectValue(List<? extends T> changes) {
        this.changes = changes;
    }

    @Column(name = "objectValueId")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "objectValueName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Change.class)
    @JoinTable(name = "ObjectValue_Changes",
            inverseJoinColumns = @JoinColumn(name = "Change_objId"))
    public List<? extends Change> getChanges() {
        return changes;
    }

    public void setChanges(List<? extends T> changes) {
        this.changes = changes;
    }
}
