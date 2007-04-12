package org.iana.rzm.common;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Embeddable
@Entity
public class TrackData implements Cloneable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private Timestamp created;
    @Basic
    private String createdBy;
    @Basic
    private Timestamp modified;
    @Basic
    private String modifiedBy;

    public TrackData() {
        createNow();
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackData trackData = (TrackData) o;

        if (created != null ? !created.equals(trackData.created) : trackData.created != null) return false;
        if (createdBy != null ? !createdBy.equals(trackData.createdBy) : trackData.createdBy != null) return false;
        if (modified != null ? !modified.equals(trackData.modified) : trackData.modified != null) return false;
        if (modifiedBy != null ? !modifiedBy.equals(trackData.modifiedBy) : trackData.modifiedBy != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (created != null ? created.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (modifiedBy != null ? modifiedBy.hashCode() : 0);
        return result;
    }


    public Object clone() throws CloneNotSupportedException {
         TrackData td = (TrackData) super.clone();
         if(td.getCreated()!=null)
         td.setCreated(new Timestamp(td.getCreated().getTime()));
         if(td.getCreatedBy()!=null)
         td.setCreatedBy(new String(td.getCreatedBy()));
            if(td.getModified()!=null)
         td.setModified(new Timestamp(td.getModified().getTime()));
        if(td.getModifiedBy()!=null)
         td.setModifiedBy(new String(td.getModifiedBy()));
         return td; 
    }
}