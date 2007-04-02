package org.iana.rzm.facade.system.trans;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ObjectValueVO extends ValueVO {

    private long id;
    private String name;
    private List<ChangeVO> changes;

    public ObjectValueVO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public List<ChangeVO> getChanges() {
        return changes;
    }

    void setChanges(List<ChangeVO> changes) {
        this.changes = changes;
    }

    public void addChanges(List<ChangeVO> changes) {
        if (this.changes == null) this.changes = new ArrayList<ChangeVO>();
        this.changes.addAll(changes);
    }
}
