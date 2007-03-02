package org.iana.rzm.facade.system;

import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.facade.user.RoleVO;

import java.util.Set;
import java.sql.Timestamp;

/**
 * A simplified version of DomainVO used with lists of domains. 
 *
 * @author Patrycja Wegrzynowicz
 */
public class SimpleDomainVO implements Trackable {

    private String name;
    private Set<RoleVO.Type> roles;

    private Long objId;
    private TrackDataVO trackData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RoleVO.Type> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleVO.Type> roles) {
        this.roles = roles;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    public void setCreated(Timestamp created) {
        trackData.setCreated(created);
    }

    public Timestamp getModified() {
        return trackData.getModified();
    }

    public void setModified(Timestamp modified) {
        trackData.setModified(modified);
    }

    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    public void setCreatedBy(String createdBy) {
        trackData.setCreatedBy(createdBy);
    }

    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }

    public void setModifiedBy(String modifiedBy) {
        trackData.setModifiedBy(modifiedBy);
    }
}
