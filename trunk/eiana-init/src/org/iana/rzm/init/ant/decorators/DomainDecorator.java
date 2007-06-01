package org.iana.rzm.init.ant.decorators;

import org.iana.rzm.domain.Domain;

import java.text.ParseException;
import java.sql.Timestamp;
import java.util.List;

import pl.nask.util.xml.XMLDateTime;

/**
 * @author: Piotr Tkaczyk
 */

public class DomainDecorator {

    private Domain domain;

    public Domain getDomain() {
        return domain;
    }

    public void setName(String name) {
        domain = new Domain(name);
    }

    public void setSupportingOrg(ContactDecorator contact) {
        domain.setSupportingOrg(contact.getContact());
    }

    public void setAdminContacts(ContactDecorator contact) {
        domain.setAdminContact(contact.getContact());
    }

    public void setAdminContacts(List<ContactDecorator> adminContacts) {
        for (ContactDecorator contact : adminContacts)
            if (contact != null)
                domain.setAdminContact(contact.getContact());
    }

    public void setTechContacts(ContactDecorator contact) {
        domain.setTechContact(contact.getContact());
    }

    public void setTechContacts(List<ContactDecorator> techContacts) {
        for (ContactDecorator contact : techContacts)
            if (contact != null)
                domain.setTechContact(contact.getContact());
    }

    public void setRegistryUrl(String registryUrl) {
        domain.setRegistryUrl(registryUrl);
    }

    public void setWhoisServer(String whoisServer) {
        if (whoisServer != null) {
            int idx = whoisServer.lastIndexOf('/');
            if (idx > -1) {
                whoisServer = whoisServer.substring(idx+1);
            }
        }
        domain.setWhoisServer(whoisServer);
    }

    public void setNameservers(HostsListDecorator hostsListDecorator) {
        for(HostDecorator hostDecorator : hostsListDecorator.getHosts())
            domain.addNameServer(hostDecorator.getHost());
    }

    public void setCreated(String value) throws ParseException {
        if (value != null && !value.equals("None"))
            domain.setCreated(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
    }

    public void setModified(String value) {
        if (value != null && !value.equals("None"))
            domain.setModified(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
    }
}
