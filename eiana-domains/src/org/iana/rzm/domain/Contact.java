package org.iana.rzm.domain;

import org.iana.rzm.common.EmailAddress;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.exceptions.InvalidEmailException;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Contact implements TrackedObject, Cloneable {

    public enum Role { AC, TC, SO }

    @Id
    @GeneratedValue
    Long id;
    @Basic
    private String name;
    @Basic
    private String organization;
    @Basic
    private String jobTitle;
    @Embedded
    private Address address;
    @Basic
    private String phoneNumber;
    @Basic
    private String altPhoneNumber;
    @Basic
    private String faxNumber;
    @Basic
    private String altFaxNumber;
    @Embedded @AttributeOverrides(value =
            {@AttributeOverride(name="email", column=@Column(name="pub_email"))}
    )
    private EmailAddress publicEmail;
    @Embedded @AttributeOverrides(value =
            {@AttributeOverride(name="email", column=@Column(name="priv_email"))}
    )
    private EmailAddress privateEmail;
    @Basic
    private boolean role;
    @Embedded
    private TrackData trackData = new TrackData();

    @Basic
    private Role domainRole;
    @ManyToOne  
    private Domain domain;

    public Contact() {
        this("");
    }

    public Contact(String name) {
        this(name, null);
    }

    public Contact(String name, String organization) {
        this.name = name;
        this.organization = organization;
    }

    public Contact(String name, String organization, Address address, String phone, String fax, String email, boolean role) throws InvalidEmailException {
        this.name = name;
        this.organization = organization;
        this.address = address;
        this.phoneNumber = phone;
        this.faxNumber = fax;
        setEmail(email);
        this.role = role;
    }

    public Long getObjId() {
        return id;
    }

    final public String getName() {
        return name;
    }

    final public void setName(String name) {
        this.name = name;
        touch();
    }

    final public void setOrganization(String organization) {
        this.organization = organization;
        touch();
    }

    final public String getOrganization() {
        return organization;
    }

    final public List<String> getSplittedOrganization() {
        if (getOrganization() != null)
            return Arrays.asList(getOrganization().split("\n"));
        return null;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        touch();
    }

    public Address getAddress() {
        return address;
    }

    final public List<String> getSplittedTextAddress() {
        if (getAddress() != null && getAddress().getTextAddress() != null)
            return Arrays.asList(getAddress().getTextAddress().split("\n"));
        return null;
    }

    public void setAddress(Address address) {
        this.address = address;
        touch();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        touch();
    }

    public String getAltPhoneNumber() {
        return altPhoneNumber;
    }

    public void setAltPhoneNumber(String altPhoneNumber) {
        this.altPhoneNumber = altPhoneNumber;
        touch();
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
        touch();
    }

    public String getAltFaxNumber() {
        return altFaxNumber;
    }

    public void setAltFaxNumber(String altFaxNumber) {
        this.altFaxNumber = altFaxNumber;
        touch();
    }

    public String getEmail() {
        return getPublicEmail();
    }

    public void setEmail(String publicEmail) throws InvalidEmailException {
        setPublicEmail(publicEmail);
    }

    public String getPublicEmail() {
        return getEmail(publicEmail);
    }

    public void setPublicEmail(String publicEmail) throws InvalidEmailException {
        this.publicEmail = toEmail(publicEmail);
        touch();
    }

    public String getPrivateEmail() {
        return getEmail(privateEmail);
    }

    public void setPrivateEmail(String privateEmail) throws InvalidEmailException {
        this.privateEmail = toEmail(privateEmail);
        touch();
    }

    final public boolean isRole() {
        return role;
    }

    final public boolean getRole() {
        return role;
    }

    final public void setRole(boolean role) {
        this.role = role;
        touch();
    }

    final public void setRole(String role) {
        this.role = Boolean.valueOf(role);
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


    public Contact clone() throws CloneNotSupportedException {
        Contact contact = (Contact) super.clone();
        contact.trackData = trackData == null ? new TrackData() : (TrackData) trackData.clone();
        contact.address = address == null ? new Address() : address.clone();
        return contact;
    }

    public void setCreated(Timestamp created) {
        trackData.setCreated(created);
    }

    public void setModified(Timestamp modified) {
        trackData.setModified(modified);
    }

    public void setCreatedBy(String createdBy) {
        trackData.setCreatedBy(createdBy);
    }

    public void setModifiedBy(String modifiedBy) {
        trackData.setModifiedBy(modifiedBy);
    }

    private String getEmail(EmailAddress email) {
        return email == null ? null : email.getEmail();
    }

    private EmailAddress toEmail(String email) throws InvalidEmailException {
        return email == null ? null : new EmailAddress(email);
    }

    void touch() {
        setModified(new Timestamp(System.currentTimeMillis()));
    }

    void checkModification(long timestamp, String modifiedBy) {
        Timestamp created = getCreated();
        Timestamp modified = getModified();
        if (created != null && created.getTime() >= timestamp) {
            setModified(null);
            setCreatedBy(modifiedBy);
        } else if (modified != null && modified.getTime() >= timestamp) {
            setModifiedBy(modifiedBy);
        }
    }

    public Role getDomainRole() {
        return domainRole;
    }

    public Domain getDomain() {
        return domain;
    }

    void setId(Long id) {
        this.id = id;
    }

    void setDomainRole(Role domainRole) {
        this.domainRole = domainRole;
    }

    void setDomain(Domain domain) {
        this.domain = domain;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (role != contact.role) return false;
        if (address != null ? !address.equals(contact.address) : contact.address != null) return false;
        if (altFaxNumber != null ? !altFaxNumber.equals(contact.altFaxNumber) : contact.altFaxNumber != null)
            return false;
        if (altPhoneNumber != null ? !altPhoneNumber.equals(contact.altPhoneNumber) : contact.altPhoneNumber != null)
            return false;
        if (domainRole != contact.domainRole) return false;
        if (faxNumber != null ? !faxNumber.equals(contact.faxNumber) : contact.faxNumber != null) return false;
        if (jobTitle != null ? !jobTitle.equals(contact.jobTitle) : contact.jobTitle != null) return false;
        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        if (organization != null ? !organization.equals(contact.organization) : contact.organization != null)
            return false;
        if (phoneNumber != null ? !phoneNumber.equals(contact.phoneNumber) : contact.phoneNumber != null) return false;
        if (privateEmail != null ? !privateEmail.equals(contact.privateEmail) : contact.privateEmail != null)
            return false;
        if (publicEmail != null ? !publicEmail.equals(contact.publicEmail) : contact.publicEmail != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (organization != null ? organization.hashCode() : 0);
        result = 31 * result + (jobTitle != null ? jobTitle.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (altPhoneNumber != null ? altPhoneNumber.hashCode() : 0);
        result = 31 * result + (faxNumber != null ? faxNumber.hashCode() : 0);
        result = 31 * result + (altFaxNumber != null ? altFaxNumber.hashCode() : 0);
        result = 31 * result + (publicEmail != null ? publicEmail.hashCode() : 0);
        result = 31 * result + (privateEmail != null ? privateEmail.hashCode() : 0);
        result = 31 * result + (role ? 1 : 0);
        result = 31 * result + (domainRole != null ? domainRole.hashCode() : 0);
        return result;
    }
}
