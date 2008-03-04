package org.iana.rzm.init.ant.decorators;

import org.iana.rzm.domain.*;
import pl.nask.util.xml.*;

import java.sql.*;
import java.text.*;
import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */

public class DomainDecorator {

    private Domain domain;
    private static final String INT = "int";
    private static final String KIM_DAVIES_ICANN_ORG = "kim.davies@icann.org";
    private static final String SIMON_RAVEH_ICANN_ORG = "simon.raveh@icann.org";
    private static final String NAELA_SARRAS_ICANN_ORG = "naela.sarras@icann.org";

    public Domain getDomain() {
        return domain;
    }

    public void setName(String name) {
        domain = new Domain(name);
    }

    public void setSupportingOrg(ContactDecorator contact) {
        updateContactEmail(contact, "Admin");
        domain.setSupportingOrg(contact.getContact());
    }

    public void setAdminContacts(ContactDecorator contact) {
        domain.setAdminContact(contact.getContact());
    }

    public void setAdminContacts(List<ContactDecorator> adminContacts) {
        for (ContactDecorator contact : adminContacts) {
            if (contact != null) {
                updateContactEmail(contact, "Admin");
                domain.setAdminContact(contact.getContact());
            }
        }
    }

    public void setTechContacts(ContactDecorator contact) {
        updateContactEmail(contact, "Tech");
        domain.setTechContact(contact.getContact());
    }

    public void setTechContacts(List<ContactDecorator> techContacts) {
        for (ContactDecorator contact : techContacts) {
            if (contact != null) {
                updateContactEmail(contact, "Tech");
                domain.setTechContact(contact.getContact());
            }
        }
    }

    public void setRegistryUrl(String registryUrl) {
        domain.setRegistryUrl(registryUrl);
    }

    public void setWhoisServer(String whoisServer) {
        if (whoisServer != null) {
            int idx = whoisServer.lastIndexOf('/');
            if (idx > -1) {
                whoisServer = whoisServer.substring(idx + 1);
            }
        }
        domain.setWhoisServer(whoisServer);
    }

    public void setNameservers(HostsListDecorator hostsListDecorator) {
        for (HostDecorator hostDecorator : hostsListDecorator.getHosts()) {
            domain.addNameServer(hostDecorator.getHost());
        }
    }

    public void setCreated(String value) throws ParseException {
        if (value != null && !value.equals("None")) {
            domain.setCreated(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
        }
    }

    public void setModified(String value) {
        if (value != null && !value.equals("None")) {
            domain.setModified(new Timestamp(new XMLDateTime(value, "dd-MMMMM-yyyy").getDate().getTime()));
        }
    }

    private void updateContactEmail(ContactDecorator contact, String s) {
        if (domain.getName().equals(INT)) {
            contact.setEmail(SIMON_RAVEH_ICANN_ORG);
        }

        //if (domain.getName().toLowerCase().startsWith("xn--")) {
        //    contact.setEmail(SIMON_RAVEH_ICANN_ORG);
        //    contact.setName(domain.getName() + "-" + s);
        //}
        
    }
}
