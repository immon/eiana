package org.iana.rzm.domain;

import org.hibernate.annotations.CollectionOfElements;
import org.iana.rzm.common.EmailAddress;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Contact implements TrackedObject,Cloneable {

    final private static List<Address> ADDR_EMPTY_LIST = Collections.unmodifiableList(new ArrayList<Address>());
    final private static List<String> STRING_EMPTY_LIST = Collections.unmodifiableList(new ArrayList<String>());

    @Basic
    private String name;
    @Basic
    private String organization;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Contact_Addresses",
            inverseJoinColumns = @JoinColumn(name = "Address_objId"))
    private List<Address> addresses;
    @CollectionOfElements
    @JoinTable(name = "Contact_PhoneNumbers")
    @Column(name = "phoneNumber", nullable = false)
    private List<String> phoneNumbers;
    @CollectionOfElements
    @JoinTable(name = "Contact_FaxNumbers")
    @Column(name = "faxNumber", nullable = false)
    private List<String> faxNumbers;
    @CollectionOfElements
    @JoinTable(name = "Contact_Emails")
    @Column(name = "email", nullable = false)
    private List<EmailAddress> emails;
    @Basic
    private boolean role;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Embedded
    private TrackData trackData = new TrackData();

    public Contact() {
        this("");
    }

    public Contact(String name) {
        this(name, "", ADDR_EMPTY_LIST, STRING_EMPTY_LIST, STRING_EMPTY_LIST, STRING_EMPTY_LIST, false);
    }

    public Contact(String name, String organization) {
        this(name, organization, ADDR_EMPTY_LIST, STRING_EMPTY_LIST, STRING_EMPTY_LIST, STRING_EMPTY_LIST, false);
    }

    public Contact(String name, Address address, String phoneNumber, String faxNumber, String email, boolean role) {
        this(name, "", Arrays.asList(address), Arrays.asList(phoneNumber), Arrays.asList(faxNumber), Arrays.asList(email), role);
    }

    public Contact(String name, String organization, Address address, String phoneNumber, String faxNumber, String email, boolean role) {
        this(name, organization, Arrays.asList(address), Arrays.asList(phoneNumber), Arrays.asList(faxNumber), Arrays.asList(email), role);
    }

    public Contact(String name, String organization, List<Address> addresses, List<String> phoneNumbers, List<String> faxNumbers, List<String> emails, boolean role) {
        this.addresses = new ArrayList<Address>();
        this.phoneNumbers = new ArrayList<String>();
        this.faxNumbers = new ArrayList<String>();
        this.emails = new ArrayList<EmailAddress>();
        
        setName(name);
        setOrganization(organization);
        setAddresses(addresses);
        setPhoneNumbers(phoneNumbers);
        setFaxNumbers(faxNumbers);
        setEmails(emails);
        setRole(role);
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    final public String getName() {
        return name;
    }

    final public void setName(String name) {
        this.name = name;
    }

    final public void setOrganization(String organization) {
        this.organization = organization;
    }

    final public String getOrganization() {
        return organization;
    }

    final public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    final public void setAddresses(Collection<Address> addresses) {
        CheckTool.checkCollectionNull(addresses, "addresses");
        this.addresses.clear();
        this.addresses.addAll(addresses);
        renumberAddresses();
    }

    final public void addAddress(Address address) {
        address.setId(addresses.size());
        this.addresses.add(address);
    }

    final public boolean removeAddress(Address address) {
        boolean ret = this.addresses.remove(address);
        renumberAddresses();
        return ret;
    }

    private void renumberAddresses() {
        int id = 0;
        for (Address addr : addresses) {
            addr.setId(id++);
        }
    }

    final public List<String> getPhoneNumbers() {
        return Collections.unmodifiableList(phoneNumbers);
    }

    final public void setPhoneNumbers(List<String> phoneNumbers) {
        CheckTool.checkCollectionNull(phoneNumbers, "phoneNumbers");
        this.phoneNumbers.clear();
        CheckTool.addAllNoDup(this.phoneNumbers, phoneNumbers);
    }

    final public void addPhoneNumber(String phoneNumber) {
        CheckTool.addNoDup(this.phoneNumbers, phoneNumber);
    }

    final public boolean removePhoneNumber(String phoneNumber) {
        return this.phoneNumbers.remove(phoneNumber);
    }

    final public List<String> getFaxNumbers() {
        return Collections.unmodifiableList(faxNumbers);
    }

    final public void setFaxNumbers(Collection<String> faxNumbers) {
        CheckTool.checkCollectionNull(faxNumbers, "faxNumbers");
        this.faxNumbers.clear();
        CheckTool.addAllNoDup(this.faxNumbers, faxNumbers);
    }

    final public void addFaxNumber(String faxNumber) {
        CheckTool.addNoDup(this.faxNumbers, faxNumber);
    }

    final public boolean removeFaxNumber(String faxNumber) {
        return this.faxNumbers.remove(faxNumber);
    }

    final public List<String> getEmails() {
        List<String> result = new ArrayList<String>();
        for (EmailAddress email : emails)
            result.add(email.getEmail());
        return Collections.unmodifiableList(result);
    }

    final public void setEmails(Collection<String> emails) {
        CheckTool.checkCollectionNull(emails, "emails");
        this.emails.clear();
        for (String email : emails)
            CheckTool.addNoDup(this.emails, new EmailAddress(email));
    }

    final public void addEmail(String email) {
        CheckTool.addNoDup(this.emails, new EmailAddress(email));
    }

    final public boolean removeEmail(String email) {
        return this.emails.remove(new EmailAddress(email));
    }

    final public boolean isRole() {
        return role;
    }

    final public boolean getRole() {
        return role;
    }

    final public void setRole(boolean role) {
        this.role = role;
    }

    final public void setRole(String role) {
        this.role = Boolean.valueOf(role);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (addresses != null ? !addresses.equals(contact.addresses) : contact.addresses != null) return false;
        if (emails != null ? !emails.equals(contact.emails) : contact.emails != null) return false;
        if (faxNumbers != null ? !faxNumbers.equals(contact.faxNumbers) : contact.faxNumbers != null) return false;
        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        if (phoneNumbers != null ? !phoneNumbers.equals(contact.phoneNumbers) : contact.phoneNumbers != null)
            return false;

        return true;
    }

    public boolean hibernateEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (role != contact.role) return false;
        if (addresses != null ? !addresses.equals(contact.addresses) : contact.addresses != null) return false;
        if (emails != null ? !emails.equals(contact.emails) : contact.emails != null) return false;
        if (faxNumbers != null ? !faxNumbers.equals(contact.faxNumbers) : contact.faxNumbers != null) return false;
        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        if (phoneNumbers != null ? !phoneNumbers.equals(contact.phoneNumbers) : contact.phoneNumbers != null)
            return false;
        if (trackData != null ? !trackData.equals(contact.trackData) : contact.trackData != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        result = 31 * result + (phoneNumbers != null ? phoneNumbers.hashCode() : 0);
        result = 31 * result + (faxNumbers != null ? faxNumbers.hashCode() : 0);
        result = 31 * result + (emails != null ? emails.hashCode() : 0);
        //result = 31 * result + (role ? 1 : 0);        
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

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }


    public Object clone() throws CloneNotSupportedException {
        Contact contact = (Contact) super.clone();
        contact.name = name;
        contact.role = role;
        contact.trackData = trackData == null ? new TrackData() : (TrackData) trackData.clone();

        List<Address> newAddresses = new ArrayList<Address>();
        if (addresses != null) {
            for(Address addr : addresses)
                newAddresses.add((Address) addr.clone());
        }
        contact.addresses = newAddresses;

        contact.phoneNumbers = listOfStringCopy(phoneNumbers);
        contact.faxNumbers = listOfStringCopy(faxNumbers);

        List<EmailAddress> newEmails = new ArrayList<EmailAddress>();
        if (emails != null) {
            for (EmailAddress email : emails)
                newEmails.add((EmailAddress) email.clone());
        }
        contact.emails = newEmails;

        return contact;
    }

    private List<String> listOfStringCopy(List<String> oldStringCollection){
        List<String> newList = new ArrayList<String>();
        if (oldStringCollection != null) {
            for(String s : oldStringCollection)
                newList.add(s);
        }
        return newList;
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
}
