package org.iana.rzm.user;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InvalidNameException;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * <p>
 * This class represents a system user role and various configuration for this role.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Role implements TrackedObject {

    public static enum Type implements UserType {
        AC, //Administrative Contact
        TC, //Technical Contact
        SO  //Supporting Organization
    }

    @Embedded
    private Name name;
    @Enumerated
    private Type type;
    @Basic
    private boolean notify;
    @Basic
    private boolean acceptFrom;
    @Basic
    private boolean mustAccept;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TrackData trackData = new TrackData();

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    final public String getName() {
        return name == null ? null : name.getName();
    }

    final public void setName(String name) throws InvalidNameException {
        this.name = new Name(name);
    }

    final public Type getType() {
        return type;
    }

    final public void setType(Type type) {
        CheckTool.checkNull(type, "type");
        this.type = type;
    }

    final public boolean isNotify() {
        return notify;
    }

    final public void setNotify(boolean notify) {
        this.notify = notify;
    }

    final public boolean isAcceptFrom() {
        return acceptFrom;
    }

    final public void setAcceptFrom(boolean acceptFrom) {
        this.acceptFrom = acceptFrom;
    }

    final public boolean isMustAccept() {
        return mustAccept;
    }

    final public void setMustAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        if (acceptFrom != role.acceptFrom) return false;
        if (mustAccept != role.mustAccept) return false;
        if (notify != role.notify) return false;
        if (name != null ? !name.equals(role.name) : role.name != null) return false;
        if (trackData != null ? !trackData.equals(role.trackData) : role.trackData != null) return false;
        if (type != role.type) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (notify ? 1 : 0);
        result = 31 * result + (acceptFrom ? 1 : 0);
        result = 31 * result + (mustAccept ? 1 : 0);
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
