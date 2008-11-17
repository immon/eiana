package org.iana.rzm.trans.change;

import org.iana.rzm.domain.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class DomainPrinter {

    public static final String DOMAIN_NAMES_SEPARATOR = ", ";

    public static String print(Domain domain) {
        if (domain == null) return "";

        StringBuffer sb = new StringBuffer();
        sb.append("TLD current state:\n");

        appendContact(sb, "Admin Contact: ", domain.getAdminContact());

        Contact technicalContact = domain.getTechContact();
        if (technicalContact != null)
            sb.append("Technical Contact: ").append(technicalContact.getName()).append("\n");

        Contact suppContact = domain.getAdminContact();
        if (suppContact != null)
            sb.append("Supporting Contact: ").append(suppContact.getName()).append("\n");

        List<Host> hosts = domain.getNameServers();

        sb.append(printNameServers(hosts));

        return sb.toString();
    }

    private static void appendContact(StringBuffer sb, String label, Contact contact) {
        if (contact != null) {
            sb.append(label).append("\n");
            sb.append("Name: ").append(contact.getName()).append("\n");
            sb.append("Organization: ").append(contact.getOrganization()).append("\n");
            sb.append("Job title: ").append(contact.getJobTitle()).append("\n");
            Address addr = contact.getAddress();
            if (addr != null) {
                sb.append("Address: ").append(addr.getTextAddress()).append(addr.getCountry()).append("\n");
            }
            sb.append("Phone number: ").append(contact.getPhoneNumber()).append("\n");
            sb.append("Alt phone number: ").append(contact.getAltPhoneNumber()).append("\n");
            sb.append("Fax number: ").append(contact.getFaxNumber()).append("\n");
            sb.append("Alt fax number: ").append(contact.getAltFaxNumber()).append("\n");
            sb.append("Public email: ").append(contact.getPublicEmail()).append("\n");
            sb.append("Private email: ").append(contact.getPrivateEmail()).append("\n");
        }
    }

    public static String printDomainNames(Set<Domain> domians) {
        StringBuffer sb = new StringBuffer();
        if (domians != null && !domians.isEmpty()) {
            for (Iterator<Domain> i = domians.iterator(); i.hasNext();) {
                Domain d = i.next();
                sb.append(d.getName());
                if (i.hasNext()) {
                    sb.append(DOMAIN_NAMES_SEPARATOR);
                }
            }
        }
        return sb.toString();
    }

    private static String printNameServers(List<Host> hosts) {
        StringBuffer sb = new StringBuffer();
        if (hosts != null) {
            if (hosts.isEmpty()) {
                sb.append("No NameServers currently assigned to TLD.");
            } else {
                sb.append("NameServers currently assigned to TLD: \n");
                for (Iterator<Host> i = hosts.iterator(); i.hasNext();) {
                    sb.append(printSingleNameServer(i.next()));
                    if (i.hasNext()) {
                        sb.append("\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    private static String printSingleNameServer(Host host) {
        StringBuffer sb = new StringBuffer();
        sb.append("NameServer: ").append(host.getName()).append("\n");
        Set<IPAddress> ipAddresses = host.getAddresses();
        if (ipAddresses == null || ipAddresses.isEmpty()) {
            sb.append("No IP Addresses currently assigned to this NameServer.");
        } else {
            sb.append("Current IP Addresses: \n");
            for (Iterator<IPAddress> i = ipAddresses.iterator(); i.hasNext();) {
                IPAddress ipAddress = i.next();
                sb.append("Type: ").append(ipAddress.getType()).append(" Address: ").append(ipAddress.getAddress());
                if (i.hasNext()) {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }
}
