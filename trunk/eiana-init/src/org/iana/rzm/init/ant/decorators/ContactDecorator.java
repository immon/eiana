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

    public Contact getContact() {
        return contact;
    }

    public void setName(String name) {
        this.contact.setName(name);
    }

    public void setOrganization(String organization) {
        this.contact.setOrganization(organization);
    }

    public void setAddress(String value) {
        Address address = new Address();
        address.setTextAddress(value);
        contact.addAddress(address);
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
        contact.setCreated(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
    }

    public void setModified(String value) {
        contact.setModified(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
    }
}
