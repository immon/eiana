package org.iana.rzm.common;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Embeddable
public class TrackData {

    private Long id;
    private Timestamp created;
    private String createdBy;
    private Timestamp modified;
    private String modifiedBy;

    public TrackData() {
        createNow();
    }

    public TrackData(long id) {
        this();
        this.id = id;
    }

    @Transient
    final public Long getId() {
        return id;
    }

    final public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "id")
    protected Long getTOId() {
        return id;
    }

    protected void setTOId(Long id) {
        this.id = id;
    }

    @Transient
    final public Timestamp getCreated() {
        return created;
    }

    final public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Column(name = "created")
    protected Timestamp getTOCreated() {
        return created;
    }

    protected void setTOCreated(Timestamp created) {
        this.created = created;
    }

    final public void createNow() {
        setCreated(currentTimestamp());
    }

    @Transient
    final public Timestamp getModified() {
        return modified;
    }

    final public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    @Column(name = "modified")
    protected Timestamp getTOModified() {
        return modified;
    }

    protected void setTOModified(Timestamp modified) {
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackData trackData = (TrackData) o;

        if (created != null ? !created.equals(trackData.created) : trackData.created != null) return false;
        if (createdBy != null ? !createdBy.equals(trackData.createdBy) : trackData.createdBy != null) return false;
        if (id != null ? !id.equals(trackData.id) : trackData.id != null) return false;
        if (modified != null ? !modified.equals(trackData.modified) : trackData.modified != null) return false;
        if (modifiedBy != null ? !modifiedBy.equals(trackData.modifiedBy) : trackData.modifiedBy != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (modifiedBy != null ? modifiedBy.hashCode() : 0);
        return result;
    }
}
