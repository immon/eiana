package org.iana.rzm.user;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * <p>
 * This class represents a user of the system, either an administrator or a normal user.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public abstract class RZMUser implements TrackedObject {

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
    @JoinColumn(name="Password_objId")
    private Password password;
    @Basic
    private boolean securID;
    // todo: securid authenticator?, pgp certificate
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TrackData trackData = new TrackData();

    protected RZMUser() {
        this(null, null, null, null, null, null, false);
    }

    protected RZMUser(String firstName, String lastName, String organization, String loginName, String email, String password, boolean securID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
        this.loginName = loginName;
        this.email = email;
        this.password = new MD5Password(password);
        this.securID = securID;
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
        return result;
    }

    public Long getId() {
        return trackData.getId();
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
