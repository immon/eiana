package org.iana.rzm.user;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * <p>This class represents a role which RZM user is allowed to posses.
 * The role determines what system actions are available to the user.</p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public abstract class Role implements TrackedObject {

    public interface Type {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    @Embedded
    private TrackData trackData = new TrackData();

    public Role() {}

    public Role(Type type) {
        CheckTool.checkNull(type, "type");
        setType(type);
    }

    public Long getObjId() {
        return objId;
    }

    public abstract boolean isAdmin();
    
    public abstract Type getType();

    public abstract void setType(Type type);

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        if (trackData != null ? !trackData.equals(role.trackData) : role.trackData != null) return false;
        if (getType() != null ? !getType().equals(role.getType()) : role.getType() != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
        return result;
    }

    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    public Timestamp getModified() {
        return trackData.getModified();
    }

    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    public TrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }
}
