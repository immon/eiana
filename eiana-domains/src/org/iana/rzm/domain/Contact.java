package org.iana.rzm.domain;

import org.hibernate.annotations.CollectionOfElements;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.*;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Contact extends TrackedObject {

    final private static List<Address> ADDR_EMPTY_LIST = Collections.unmodifiableList(new ArrayList<Address>());
    final private static List<String> STRING_EMPTY_LIST = Collections.unmodifiableList(new ArrayList<String>());

    private String name;
    private List<Address> addresses;
    private List<String> phoneNumbers;
    private List<String> faxNumbers;
    private List<String> emails;
    private boolean role;

    public Contact() {
        this("");
    }

    public Contact(String name) {
        this(name, ADDR_EMPTY_LIST, STRING_EMPTY_LIST, STRING_EMPTY_LIST, STRING_EMPTY_LIST, false);
    }

    public Contact(String name, Address address, String phoneNumber, String faxNumber, String email, boolean role) {
        this(name, Arrays.asList(address), Arrays.asList(phoneNumber), Arrays.asList(faxNumber), Arrays.asList(email), role);
    }

    public Contact(String name, List<Address> addresses, List<String> phoneNumbers, List<String> faxNumbers, List<String> emails, boolean role) {
        this.addresses = new ArrayList<Address>();
        this.phoneNumbers = new ArrayList<String>();
        this.faxNumbers = new ArrayList<String>();
        this.emails = new ArrayList<String>();
        
        setName(name);
        setAddresses(addresses);
        setPhoneNumbers(phoneNumbers);
        setFaxNumbers(faxNumbers);
        setEmails(emails);
        setRole(role);
    }

    @Transient
    final public String getName() {
        return name;
    }

    final public void setName(String name) {
        this.name = name;
    }

    protected String getContactName() {
        return name;
    }

    protected void setContactName(String name) {
        this.name = name;
    }

    @Transient
    final public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    final public void setAddresses(Collection<Address> addresses) {
        CheckTool.checkCollectionNull(phoneNumbers, "addresses");
        this.addresses.clear();
        this.addresses.addAll(addresses);
    }

    final public void addAddress(Address address) {
        this.addresses.add(address);
    }

    final public boolean removeAddress(Address address) {
        return this.addresses.remove(address);
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Contact_Addresses",
            inverseJoinColumns = @JoinColumn(name = "Address_objId"))
    protected List<Address> getAddressesList() {
        return addresses;
    }
    
    protected void setAddressesList(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Transient
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

    @CollectionOfElements
    @JoinTable(name = "Contact_PhoneNumbers")
    @Column(name = "phoneNumber", nullable = false)
    protected List<String> getPhoneNumbersList() {
        return phoneNumbers;
    }

    protected void setPhoneNumbersList(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Transient
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

    @CollectionOfElements
    @JoinTable(name = "Contact_FaxNumbers")
    @Column(name = "faxNumber", nullable = false)
    protected List<String> getFaxNumbersList() {
        return faxNumbers;    
    }

    protected void setFaxNumbersList(List<String> faxNumbers) {
        this.faxNumbers = faxNumbers; 
    }

    @Transient
    final public List<String> getEmails() {
        return Collections.unmodifiableList(emails);
    }

    final public void setEmails(Collection<String> emails) {
        CheckTool.checkCollectionNull(emails, "emails");
        this.emails.clear();
        CheckTool.addAllNoDup(this.emails, emails);
    }

    final public void addEmail(String email) {
        CheckTool.addNoDup(this.emails, email);
    }

    final public boolean removeEmail(String email) {
        return this.emails.remove(email);
    }

    @CollectionOfElements
    @JoinTable(name = "Contact_Emails")
    @Column(name = "email", nullable = false)
    protected List<String> getEmailsList() {
        return emails;
    }

    protected void setEmailsList(List<String> emails) {
        this.emails = emails;
    }

    @Transient
    final public boolean isRole() {
        return role;
    }

    final public void setRole(boolean role) {
        this.role = role;
    }

    protected boolean isContactRole() {
        return role;
    }

    protected void setContactRole(boolean role) {
        this.role = role;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}
