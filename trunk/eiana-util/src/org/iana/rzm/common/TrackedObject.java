package org.iana.rzm.common;

import java.sql.Timestamp;

public abstract class TrackedObject {

    private Long id;
    private Timestamp created;
    private String createdBy;
    private Timestamp modified;
    private String modifiedBy;

    public TrackedObject() {
        createNow();
    }

    public TrackedObject(long id) {
        this();
        this.id = id;
    }

    final public Long getId() {
        return id;
    }

    final public void setId(Long id) {
        this.id = id;
    }

    final public Timestamp getCreated() {
        return created;
    }

    final public void setCreated(Timestamp created) {
        this.created = created;
    }

    final public void createNow() {
        setCreated(currentTimestamp());
    }

    final public Timestamp getModified() {
        return modified;
    }

    final public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
