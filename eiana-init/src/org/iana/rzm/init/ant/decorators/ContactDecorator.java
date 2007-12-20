package org.iana.rzm.init.ant.decorators;

import org.iana.rzm.domain.*;
import org.iana.rzm.init.ant.SpringInitContext;
import org.iana.rzm.facade.common.cc.CountryCodes;
import org.iana.codevalues.Value;
import pl.nask.util.xml.*;

import java.sql.*;
import java.text.*;
import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class ContactDecorator {

    private Contact contact = new Contact();

    List<String> orgLines = new ArrayList<String>();
    List<String> addressLines = new ArrayList<String>();
    String city;
    String state;
    String country;
    String postCode;

    static Map<String, String> countries;

    private void flushAddressChange() {
        Address address = new Address();

        StringBuffer text = new StringBuffer();
        for (String line : addressLines) {
            text.append(line).append("\n");
        }
        if (!isEmpty(city) || !isEmpty(postCode)) {
            if (!isEmpty(city)) text.append(city).append(" ");
            if (!isEmpty(state)) text.append(state).append(" ");
            text.append("\n");
        }
        //if (!isEmpty(country)) text.append(country).append("\n");
        if (countries == null) {
            countries = new HashMap<String, String>();
            CountryCodes cc = (CountryCodes) SpringInitContext.getContext().getBean("cc");
            for (Value c : cc.getCountries()) {
                countries.put(c.getValueName(), c.getValueId());
            }
        }
        // warning: country is a full name - not a country code!
        address.setCountryCode(countries.get(country));
        address.setTextAddress(text.toString());

        contact.setAddress(address);
    }

    private void flushNameChange() {
        if (orgLines.size() > 0) {
            StringBuffer buf = new StringBuffer();
            for (String line : orgLines) {
                buf.append(line).append("\n");
            }
            setOrganization(buf.toString());
        }
    }

    private boolean isEmpty(String value) {
        return (value == null) || (value.trim().length() == 0);
    }

    public Contact getContact() {
        return contact;
    }

    public void setName(String name) {
        this.contact.setName(name);
    }

    public void setOrganization(String organization) {
        String name = this.contact.getName();
        if (isEmpty(name)) this.contact.setName(organization);
        this.contact.setOrganization(organization);
    }

    public List<String> getOrgLines() {
        return orgLines;
    }

    public void setOrgLines(List<String> orgLines) {
        this.orgLines = orgLines;
    }

    public List<String> getAddressLines() {
        return addressLines;
    }

    public void setAddressLines(List<String> addressLines) {
        this.addressLines = addressLines;
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
        contact.setPhoneNumber(value);
    }

    public void setFax(String value) {
        contact.setFaxNumber(value);
    }

    public void setEmail(String value) {
        if (value.contains(" or ")) {
            List<String> emails = new ArrayList(Arrays.asList(value.split(" or ")));
            for (String email : emails)
                contact.setEmail(email.trim());
        } else if (value.contains("; ")) {
            List<String> emails = new ArrayList(Arrays.asList(value.split("; ")));
            for (String email : emails)
                contact.setEmail(email.trim());
        } else if (value.contains(", ")) {
            List<String> emails = new ArrayList(Arrays.asList(value.split(", ")));
            for (String email : emails)
                contact.setEmail(email.trim());
        } else if (value.contains("/ ")) {
            List<String> emails = new ArrayList(Arrays.asList(value.split("/ ")));
            for (String email : emails)
                contact.setEmail(email.trim());
        } else
            contact.setEmail(value);
    }

    public void setCreated(String value) throws ParseException {
        // cr-date is always set and is after address so we can flush address now
        flushAddressChange();
        flushNameChange();
        contact.setCreated(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
    }

    public void setModified(String value) {
        contact.setModified(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
    }
}
