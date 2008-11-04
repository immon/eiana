package org.iana.rzm.trans.change;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.IPAddress;

import java.util.List;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class DomainPrinter {

    public static String print(Domain domain) {
        if (domain == null) return "";

        StringBuffer sb = new StringBuffer();
        sb.append("TLD current state:\n");

        Contact adminContact = domain.getAdminContact();
        if (adminContact != null)
            sb.append("Admin Contact: ").append(adminContact.getName()).append("\n");

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
