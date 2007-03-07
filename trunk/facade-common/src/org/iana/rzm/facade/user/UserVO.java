package org.iana.rzm.facade.user;

import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.facade.common.TrackDataVO;

import java.util.Set;
import java.util.HashSet;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 * @author Marcin Zajaczkowski
 */
public class UserVO implements Trackable {

    private String userName;
    private String firstName;
    private String lastName;
    private String organization;
    private Set<RoleVO> roles;

    private Long objId;
    private TrackDataVO trackData = new TrackDataVO();
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public boolean isAdmin() {
        // todo: check if contains one of Admin roles...
        return false;
    }

    public Set<RoleVO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleVO> roles) {
        this.roles = roles;
    }

    public void addRole(RoleVO role) {
        if (this.roles == null) {
            this.roles = new HashSet<RoleVO>();
        }
        this.roles.add(role);
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
