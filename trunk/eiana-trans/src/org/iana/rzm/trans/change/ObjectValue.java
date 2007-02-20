package org.iana.rzm.trans.change;

import java.util.List;

public class ObjectValue<T extends Change> implements Value<T> {

    private long id;
    private String name;
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
