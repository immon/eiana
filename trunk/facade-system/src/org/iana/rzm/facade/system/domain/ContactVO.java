package org.iana.rzm.facade.system.domain;

import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.common.EmailAddress;

import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactVO implements Trackable, Serializable {

    private String name;
    private String organization;
    private List<AddressVO> addresses;
    private List<String> phoneNumbers;
    private List<String> faxNumbers;
    private List<EmailAddress> emails;
    private boolean role;

    private Long objId;
    private TrackDataVO trackData = new TrackDataVO();

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

    public List<AddressVO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressVO> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(AddressVO address) {
        if (addresses == null)
            addresses = new ArrayList<AddressVO>();
        addresses.add(address);
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void addPhoneNumber(String number) {
        if (phoneNumbers == null)
            phoneNumbers = new ArrayList<String>();
        phoneNumbers.add(number);
    }

    public List<String> getFaxNumbers() {
        return faxNumbers;
    }

    public void setFaxNumbers(List<String> faxNumbers) {
        this.faxNumbers = faxNumbers;
    }

    public void addFaxNumber(String number) {
        if (faxNumbers == null)
            faxNumbers = new ArrayList<String>();
        faxNumbers.add(number);
    }

    public List<String> getEmails() {
        List<String> result = new ArrayList<String>();
        if (emails != null)
            for (EmailAddress emailAddress : emails)
                result.add(emailAddress.getEmail());
        return result;
    }

    public void setEmails(List<String> emails) {
        this.emails = new ArrayList<EmailAddress>();
        if (emails != null)
            for (String email : emails)
                this.emails.add(new EmailAddress(email));
    }

    public void addEmail(String email) {
        if (emails == null)
            emails = new ArrayList<EmailAddress>();
        emails.add(new EmailAddress(email));
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
        if (addresses != null ? !addresses.equals(contactVO.addresses) : contactVO.addresses != null) return false;
        if (emails != null ? !emails.equals(contactVO.emails) : contactVO.emails != null) return false;
        if (faxNumbers != null ? !faxNumbers.equals(contactVO.faxNumbers) : contactVO.faxNumbers != null) return false;
        if (name != null ? !name.equals(contactVO.name) : contactVO.name != null) return false;
        if (objId != null ? !objId.equals(contactVO.objId) : contactVO.objId != null) return false;
        if (organization != null ? !organization.equals(contactVO.organization) : contactVO.organization != null)
            return false;
        if (phoneNumbers != null ? !phoneNumbers.equals(contactVO.phoneNumbers) : contactVO.phoneNumbers != null)
            return false;
        if (trackData != null ? !trackData.equals(contactVO.trackData) : contactVO.trackData != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (organization != null ? organization.hashCode() : 0);
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        result = 31 * result + (phoneNumbers != null ? phoneNumbers.hashCode() : 0);
        result = 31 * result + (faxNumbers != null ? faxNumbers.hashCode() : 0);
        result = 31 * result + (emails != null ? emails.hashCode() : 0);
        result = 31 * result + (role ? 1 : 0);
        result = 31 * result + (objId != null ? objId.hashCode() : 0);
        result = 31 * result + (trackData != null ? trackData.hashCode() : 0);
        return result;
    }
}
