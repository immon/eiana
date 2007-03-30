package org.iana.rzm.user;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * <p/>
 * This class represents a user of the system, either an administrator or a normal user.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class RZMUser implements TrackedObject {

    @Basic
    private String firstName;
    @Basic
    private String lastName;
    @Basic
    private String organization;
    @Basic
    private String loginName;
    @Basic
    private String email;
    @ManyToOne(cascade = CascadeType.ALL, targetEntity = MD5Password.class)
    @JoinColumn(name = "Password_objId")
    private Password password;
    @Basic
    private boolean securID;
    // todo: securid authenticator?, pgp certificate
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TrackData trackData = new TrackData();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "RZMUser_Roles",
            inverseJoinColumns = @JoinColumn(name = "Role_objId"))
    private List<Role> roles;

    public RZMUser() {
        this(null, null, null, null, null, null, false);
    }

    public RZMUser(String firstName, String lastName, String organization, String loginName, String email, String password, boolean securID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
        this.loginName = loginName;
        this.email = email;
        this.password = new MD5Password(password);
        this.securID = securID;
        this.roles = new ArrayList<Role>();
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
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

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password.setPassword(password);
    }

    public boolean isValidPassword(String password) {
        return this.password.isValid(password);
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public boolean isSecurID() {
        return securID;
    }

    public void setSecurID(boolean securID) {
        this.securID = securID;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RZMUser user = (RZMUser) o;

        if (securID != user.securID) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (loginName != null ? !loginName.equals(user.loginName) : user.loginName != null) return false;
        if (organization != null ? !organization.equals(user.organization) : user.organization != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (trackData != null ? !trackData.equals(user.trackData) : user.trackData != null) return false;
        if (roles != null ? !roles.equals(user.roles) : user.roles != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (organization != null ? organization.hashCode() : 0);
        result = 31 * result + (loginName != null ? loginName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (securID ? 1 : 0);
        result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
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

    //marcinz: has to be public?
    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }

    final public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    final public void setRoles(List<Role> roles) {
        this.roles.clear();
        if (roles != null) this.roles.addAll(roles);
    }

    final public void addRole(Role role) {
        this.roles.add(role);
    }

    final public boolean removeRole(Role role) {
        return this.roles.remove(role);
    }

    final public void clearRoles() {
        this.roles.clear();
    }

    final public boolean mustAccept(String name, Role.Type type) {
        if (type instanceof SystemRole.SystemType) {
            for (Role role : roles)
                if (!role.isAdmin()) {
                    SystemRole sr = (SystemRole) role;
                    if (name.equals(sr.getName()) && type.equals(sr.getType())
                            && sr.isMustAccept())
                        return true;
                }
        }
        return false;
    }

    final public boolean isEligibleToAccept(String name, Role.Type type) {
        for (Role role : roles)
            if (role.isAdmin() && type instanceof AdminRole.AdminType) {
                AdminRole ar = (AdminRole) role;
                if (type.equals(ar.getType()))
                    return true;
            } else if (!role.isAdmin() && type instanceof SystemRole.SystemType) {
                SystemRole sr = (SystemRole) role;
                if (name.equals(sr.getName()) && type.equals(sr.getType())
                        && sr.isAcceptFrom())
                    return true;
            }
        return false;
    }
}