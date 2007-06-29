package org.iana.rzm.facade.system.domain;

import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.common.EmailAddress;
import org.iana.rzm.common.exceptions.InvalidEmailException;

import java.sql.Timestamp;
import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactVO implements Trackable, Serializable {

    private String name;
    private String organization;
    private String jobTitle;
    private AddressVO address;
    private String phoneNumber;
    private String altPhoneNumber;
    private String faxNumber;
    private String altFaxNumber;
    private EmailAddress publicEmail;
    private EmailAddress privateEmail;
    private boolean role;

    private Long objId;
    private TrackDataVO trackData = new TrackDataVO();

    public ContactVO() {
    }

    public ContactVO(String name, String publicEmail) throws InvalidEmailException {
        this.name = name;
        this.publicEmail = toEmail(publicEmail);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public AddressVO getAddress() {
        return address;
    }

    public void setAddress(AddressVO address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAltPhoneNumber() {
        return altPhoneNumber;
    }

    public void setAltPhoneNumber(String altPhoneNumber) {
        this.altPhoneNumber = altPhoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getAltFaxNumber() {
        return altFaxNumber;
    }

    public void setAltFaxNumber(String altFaxNumber) {
        this.altFaxNumber = altFaxNumber;
    }

    public String getPublicEmail() {
        return getEmail(publicEmail);
    }

    public void setPublicEmail(String publicEmail) throws InvalidEmailException {
        this.publicEmail = toEmail(publicEmail);
    }

    public String getPrivateEmail() {
        return getEmail(privateEmail);
    }

    public void setPrivateEmail(String privateEmail) throws InvalidEmailException {
        this.privateEmail = toEmail(privateEmail);
    }

    public String getEmail() {
        return getPublicEmail();
    }
    
    public void setEmail(String email) throws InvalidEmailException {
        setPublicEmail(email);
    }

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactVO contactVO = (ContactVO) o;

        if (role != contactVO.role) return false;
        if (address != null ? !address.equals(contactVO.address) : contactVO.address != null) return false;
        if (altFaxNumber != null ? !altFaxNumber.equals(contactVO.altFaxNumber) : contactVO.altFaxNumber != null)
            return false;
        if (altPhoneNumber != null ? !altPhoneNumber.equals(contactVO.altPhoneNumber) : contactVO.altPhoneNumber != null)
            return false;
        if (faxNumber != null ? !faxNumber.equals(contactVO.faxNumber) : contactVO.faxNumber != null) return false;
        if (name != null ? !name.equals(contactVO.name) : contactVO.name != null) return false;
        if (objId != null ? !objId.equals(contactVO.objId) : contactVO.objId != null) return false;
        if (organization != null ? !organization.equals(contactVO.organization) : contactVO.organization != null)
            return false;
        if (phoneNumber != null ? !phoneNumber.equals(contactVO.phoneNumber) : contactVO.phoneNumber != null)
            return false;
        if (privateEmail != null ? !privateEmail.equals(contactVO.privateEmail) : contactVO.privateEmail != null)
            return false;
        if (publicEmail != null ? !publicEmail.equals(contactVO.publicEmail) : contactVO.publicEmail != null)
            return false;
        if (trackData != null ? !trackData.equals(contactVO.trackData) : contactVO.trackData != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (organization != null ? organization.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (altPhoneNumber != null ? altPhoneNumber.hashCode() : 0);
        result = 31 * result + (faxNumber != null ? faxNumber.hashCode() : 0);
        result = 31 * result + (altFaxNumber != null ? altFaxNumber.hashCode() : 0);
        result = 31 * result + (publicEmail != null ? publicEmail.hashCode() : 0);
        result = 31 * result + (privateEmail != null ? privateEmail.hashCode() : 0);
        result = 31 * result + (role ? 1 : 0);
        result = 31 * result + (objId != null ? objId.hashCode() : 0);
        result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
        return result;
    }

    private String getEmail(EmailAddress email) {
        return email == null ? null : email.getEmail();
    }

    private EmailAddress toEmail(String email) throws InvalidEmailException {
        return email == null ? null : new EmailAddress(email);
    }
}
