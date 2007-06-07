package org.iana.rzm.facade.user;

import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.facade.common.TrackDataVO;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.TreeSet;
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
    private String email;
    private Set<RoleVO> roles = new HashSet<RoleVO>();

    private Long objId;
    private TrackDataVO trackData = new TrackDataVO();

    private String publicKey;

    public UserVO() {
    }

    public UserVO(String userName) {
        this.userName = userName;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        if (roles == null) return false;

        for (RoleVO role : roles) {
            if (role.isAdmin()) return true;
        }

        return false;
    }

    public Set<String> getRoleDomainNames() {
        Set<String> ret = new TreeSet<String>();
        for (RoleVO role : roles) {
            if (role instanceof SystemRoleVO) {
                SystemRoleVO sys = (SystemRoleVO) role;
                ret.add(sys.getName());
            }
        }
        return ret;
    }

    public boolean hasRole(RoleVO role) {
        return roles.contains(role);
    }

    public boolean hasAnyRole(Set<RoleVO> roles) {
        for (RoleVO role : roles) {
            if (hasRole(role)) return true;
        }
        return false;
    }

    public Set<RoleVO> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void setRoles(Set<RoleVO> roles) {
        this.roles.clear();
        if (roles != null) this.roles.addAll(roles);
    }

    public void addRole(RoleVO role) {
        this.roles.add(role);
    }

    public void removeRole(RoleVO role) {
        this.roles.remove(role);
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean hasAccessToDomain(String domainName) {
        for (RoleVO role : roles) {
            if (role instanceof SystemRoleVO) {
                SystemRoleVO sys = (SystemRoleVO) role;
                if (sys.isAccessToDomain() && domainName.equals(sys.getName()))
                    return true;
            }
        }
        return false;
    }
}
