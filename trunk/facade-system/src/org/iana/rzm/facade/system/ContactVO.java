package org.iana.rzm.facade.system;

import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.common.Trackable;

import java.util.List;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactVO implements Trackable {

    private String name;
    private List<AddressVO> addresses;
    private List<String> phoneNumbers;
    private List<String> faxNumbers;
    private List<String> emails;
    private boolean role;

    private long objId;
    private TrackDataVO trackData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    public long getObjId() {
        return objId;
    }

    public void setObjId(long objId) {
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
