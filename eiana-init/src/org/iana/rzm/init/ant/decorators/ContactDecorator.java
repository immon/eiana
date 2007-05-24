package org.iana.rzm.init.ant.decorators;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Address;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.text.ParseException;

import pl.nask.util.xml.XMLDateTime;

/**
 * @author: Piotr Tkaczyk
 */
public class ContactDecorator {

    private Contact contact = new Contact();

    String address;
    String city;
    String state;
    String country;
    String postCode;

    private void flushAddressChange() {
        Address address = new Address();

        StringBuffer text = new StringBuffer();
        if (this.address != null) text.append(this.address).append("<br/>");
        if (city != null || postCode != null) {
            if (city != null) text.append(city).append(" ");
            if (state != null) text.append(state).append(" ");
            text.append("<br/>");
        }
        if (country != null) text.append(country).append("<br/>");
        address.setTextAddress(text.toString());

        // warning: country is a full name - not a country code!
        // set up a correct country code

        contact.addAddress(address);
    }

    public Contact getContact() {
        return contact;
    }

    public void setName(String name) {
        this.contact.setName(name);
    }

    public void setOrganization(String organization) {
        this.contact.setOrganization(organization);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setPhone(String value) {
        contact.addPhoneNumber(value);
    }

    public void setFax(String value) {
        contact.addFaxNumber(value);
    }

    public void setEmail(String value) {
        if(value.contains(" or ")) {
            List<String> emails = new ArrayList(Arrays.asList(value.split(" or ")));
            for (String email : emails)
                contact.addEmail(email.trim());
        } else
        if(value.contains("; ")) {
            List<String> emails = new ArrayList(Arrays.asList(value.split("; ")));
            for (String email : emails)
                contact.addEmail(email.trim());
        } else
        if(value.contains(", ")) {
            List<String> emails = new ArrayList(Arrays.asList(value.split(", ")));
            for (String email : emails)
                contact.addEmail(email.trim());
        } else
        if(value.contains("/ ")) {
            List<String> emails = new ArrayList(Arrays.asList(value.split("/ ")));
            for (String email : emails)
                contact.addEmail(email.trim());
        } else
            contact.addEmail(value);
    }

    public void setCreated(String value) throws ParseException {
        // cr-date is always set and is after address so we can flush address now
        flushAddressChange();
        contact.setCreated(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
    }

    public void setModified(String value) {
        contact.setModified(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
    }
}
