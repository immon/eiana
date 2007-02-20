package org.iana.rzm.facade.system;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ObjectValueVO extends ValueVO {

    private long id;
    private String name;
    private List<ChangeVO> changes;

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

    public List<ChangeVO> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeVO> changes) {
        this.changes = changes;
    }
}
