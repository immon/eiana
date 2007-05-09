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

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getFaxNumbers() {
        return faxNumbers;
    }

    public void setFaxNumbers(List<String> faxNumbers) {
        this.faxNumbers = faxNumbers;
    }

    public List<String> getEmails() {
        List<String> result = new ArrayList<String>();
        for (EmailAddress emailAddress : emails)
            result.add(emailAddress.getEmail());
        return result;
    }

    public void setEmails(List<String> emails) {
        this.emails = new ArrayList<EmailAddress>(); 
        for (String email : emails)
            this.emails.add(new EmailAddress(email));
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
}
