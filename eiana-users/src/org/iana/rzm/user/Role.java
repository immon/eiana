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
        AC, TC, SO
    }

    private Name name;
    private Type type;
    private boolean notify;
    private boolean acceptFrom;
    private boolean mustAccept;
    private Long objId;
    private TrackData trackData = new TrackData();

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    @Transient
    final public String getName() {
        return name == null ? null : name.getName();
    }

    final public void setName(String name) throws InvalidNameException {
        this.name = new Name(name);
    }

    @Embedded
    @AttributeOverride(name = "nameStr",
            column = @Column(name = "name"))
    private Name getRoleName() {
        return name;
    }

    private void setRoleName(Name name) {
        this.name = name;
    }

    @Transient
    final public Type getType() {
        return type;
    }

    final public void setType(Type type) {
        CheckTool.checkNull(type, "type");
        this.type = type;
    }

    private Type getRoleType() {
        return type;
    }

    private void setRoleType(Type type) {
        this.type = type;
    }

    @Transient
    final public boolean isNotify() {
        return notify;
    }

    final public void setNotify(boolean notify) {
        this.notify = notify;
    }

    private boolean isRoleNotify() {
        return notify;
    }

    private void setRoleNotify(boolean notify) {
        this.notify = notify;
    }

    @Transient
    final public boolean isAcceptFrom() {
        return acceptFrom;
    }

    final public void setAcceptFrom(boolean acceptFrom) {
        this.acceptFrom = acceptFrom;
    }

    private boolean isRoleAcceptFrom() {
        return acceptFrom;
    }

    private void setRoleAcceptFrom(boolean acceptFrom) {
        this.acceptFrom = acceptFrom;
    }

    @Transient
    final public boolean isMustAccept() {
        return mustAccept;
    }

    final public void setMustAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }

    private boolean isRoleMustAccept() {
        return mustAccept;
    }

    private void setRoleMustAccept(boolean mustAccept) {
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

    @Transient
    public Long getId() {
        return trackData.getId();
    }

    @Transient
    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    @Transient
    public Timestamp getModified() {
        return trackData.getModified();
    }

    @Transient
    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    @Transient
    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    @Embedded
    public TrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }
}
