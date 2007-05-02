package org.iana.rzm.facade.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class TrackDataVO implements Trackable, Serializable {

    private Timestamp created;
    private Timestamp modified;
    private String createdBy;
    private String modifiedBy;

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
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

        TrackDataVO that = (TrackDataVO) o;

        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (modified != null ? !modified.equals(that.modified) : that.modified != null) return false;
        if (modifiedBy != null ? !modifiedBy.equals(that.modifiedBy) : that.modifiedBy != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (created != null ? created.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (modifiedBy != null ? modifiedBy.hashCode() : 0);
        return result;
    }
}
