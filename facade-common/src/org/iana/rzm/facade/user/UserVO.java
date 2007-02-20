package org.iana.rzm.facade.user;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class UserVO {

    private String userName;
    private String firstName;
    private String lastName;
    private String organization;
    private Set<RoleVO> roles;

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
}
