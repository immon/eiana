package org.iana.rzm.trans.change;

import org.iana.rzm.domain.*;

import java.util.*;

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
        appendContact(sb, "Technical Contact: ", domain.getTechContact());
        appendContact(sb, "Supporting Contact: ", domain.getSupportingOrg());

        List<Host> hosts = domain.getNameServers();
        sb.append(printNameServers(hosts));

        return sb.toString();
    }

    private static void appendContact(StringBuffer sb, String label, Contact contact) {

        if (contact != null) {
            append(sb, label, true);
            append(sb, "Name: ", false);
            append(sb, contact.getName(), true);
            append(sb,"Organization: ", false);
            append(sb, contact.getOrganization(), true);
            append(sb, "Job title: ", false);
            append(sb, contact.getJobTitle(), true);
            Address addr = contact.getAddress();
            if (addr != null) {
                sb.append("Address: ").append(addr.getTextAddress()).append(addr.getCountry()).append("\n");
            }
            append(sb,"Phone number: ", false);
            append(sb, contact.getPhoneNumber(), true);
            append(sb, "Alt phone number: ", false);
            append(sb, contact.getAltPhoneNumber(), true);
            append(sb, "Fax number: ", false);
            append(sb, contact.getFaxNumber(), true);
            append(sb, "Alt fax number: ", false);
            append(sb, contact.getAltFaxNumber(), true);
            append(sb, "Public email: ", false);
            append(sb, contact.getPublicEmail(), true);
            append(sb, "Private email: ", false);
            append(sb, contact.getPrivateEmail(), true);
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

    private static void append(StringBuffer buffer, String str, boolean endofLine ){
        if(buffer == null){
            return;
        }

        if(str != null){
            buffer.append(str);
        }else{
            buffer.append(" ");
        }

        if(endofLine){
            buffer.append("\n");
        }

        return;
    }
}
