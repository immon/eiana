package org.iana.rzm.facade.system;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactVO {

    private String name;
    private List<AddressVO> addresses;
    private List<String> phoneNumbers;
    private List<String> faxNumbers;
    private List<String> emails;
    private boolean role;

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
}
