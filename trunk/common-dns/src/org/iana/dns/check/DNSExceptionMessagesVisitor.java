package org.iana.dns.check;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.exceptions.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 * @author Piotr Tkaczyk
 */
public class DNSExceptionMessagesVisitor implements DNSTechnicalCheckExceptionVisitor {
    private StringBuffer buffer = new StringBuffer();

    public void acceptMinimumNetworkDiversityException(MinimumNetworkDiversityException e) {
        if (e.getASNumber() == null) {
            buffer.append("Unable to identify the AS number for the following name servers: ");
            for (DNSHost host : e.getHosts())
                buffer.append(host.getName()).append("\n");
        } else {
            buffer.append("All name servers have the same AS number (")
                    .append(e.getASNumber()).append(") for domain: ")
                    .append(domainTLDName(e.getDomainName())).append(":\n");
            for (DNSHost host : e.getHosts())
                buffer.append(host.getName()).append("\n");
        }
    }

    public void acceptNotUniqueIPAddressException(NotUniqueIPAddressException e) {
        buffer.append("IP addresses are duplicated for hosts: ").append(e.getHostName())
                .append(", ").append(e.getOtherHost().getName())
                .append(" and domain: ").append(domainTLDName(e.getDomainName())).append("\n");
    }

    public void acceptEmptyIPAddressListException(EmptyIPAddressListException e) {
        buffer.append("Host has no IP's: ").append(e.getHostName())
                .append(" for domain: ").append(e.getHostName()).append("\n");
    }

    public void acceptMaximumPayloadSizeExceededException(MaximumPayloadSizeExceededException e) {
        buffer.append("Response estimated size is greater than 512 bytes: ")
                .append(e.getEstimatedSize()).append(" for domain: ")
                .append(domainTLDName(e.getDomainName())).append("\n");
    }

    public void acceptNameServerCoherencyException(NameServerCoherencyException e) {
        buffer.append("Supplied name servers names: ").append(e.getDomain().getNameServerNames()).append(" don't match names")
                .append(" returned by: ").append(e.getHostName()).append(" in NS Resource Record: ")
                .append(e.getReceivedNameServers()).append(" for domain: ").append(domainTLDName(e.getDomainName())).append("\n");
    }

    public void acceptNoASNumberException(NoASNumberException e) {
        buffer.append("There is no AS number for IP address: ")
                .append(e.getIpAddress().getAddress()).append(", host: ")
                .append(e.getHostName()).append(" and domain: ").append(domainTLDName(e.getDomainName())).append("\n");
    }

    public void acceptNotEnoughNameServersException(NotEnoughNameServersException e) {
        buffer.append("The number of unique name servers is lower than the minimum required for domain: ")
                .append(domainTLDName(e.getDomainName())).append("\n");
    }

    public void acceptReservedIPv4Exception(ReservedIPv4Exception e) {
        buffer.append("IP address is restricted according to RFC 3330: ")
                .append(e.getIpAddress().getAddress()).append(" for host: ").append(e.getHostName())
                .append(" and domain: ").append(domainTLDName(e.getDomainName())).append("\n");
    }

    public void acceptSerialNumberNotEqualException(SerialNumberNotEqualException e) {
        buffer.append("There are name servers with more then one serial number for domain: ")
                .append(domainTLDName(e.getDomainName())).append(":\n");
        for (Map.Entry<Long, List<DNSHost>> entry : e.getSerialsMap().entrySet()) {
            buffer.append("serial number: ").append(entry.getKey()).append("\n");
            for (DNSHost host : entry.getValue())
                buffer.append("\thost: ").append(host.getName()).append("\n");
        }
    }

    public void acceptNameServerIPAddressesNotEqualException(NameServerIPAddressesNotEqualException e) {
        buffer.append("For ").append(e.getHostName()).append(", the A/AAAA records of the authoritative name servers [");
        for (Iterator<DNSIPAddress> iterator = e.getReturnedIPAddresses().iterator(); iterator.hasNext();) {
            buffer.append(iterator.next().getAddress());
            if (iterator.hasNext()) buffer.append(", ");
        }
        buffer.append("] don't match the supplied glue records for inclusion in the root zone [");
        for (Iterator<String> iterator = e.getHost().getIPAddressesAsStrings().iterator(); iterator.hasNext();) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) buffer.append(", ");
        }
        buffer.append("].").append("\n");
    }

    public void acceptNameServerTechnicalCheckException(NameServerTechnicalCheckException e) {
    }

    public void acceptNameServerUnreachableException(NameServerUnreachableException e) {
        buffer.append("SOA record in unreachable for host: ")
                .append(e.getHostName()).append("\n");
    }

    public void acceptNameServerUnreachableByTCPException(NameServerUnreachableByTCPException e) {
        buffer.append("SOA record in unreachable by TCP for host: ")
                .append(e.getHostName()).append("\n");
    }

    public void acceptNameServerUnreachableByUDPException(NameServerUnreachableByUDPException e) {
        buffer.append("SOA record in unreachable by UDP for host: ")
                .append(e.getHostName()).append("\n");
    }

    public void acceptNotAuthoritativeNameServerException(NotAuthoritativeNameServerException e) {
        buffer.append("Host is not authoritative: ").append(e.getHostName())
                .append(" for domain: ").append(domainTLDName(e.getDomainName())).append("\n");
    }

    public void acceptWhoIsIOException(WhoIsIOException e) {
        buffer.append("Unexpected error checking WhoIs, host: ").append(e.getHostName())
                .append(", IP: ").append(e.getIpAddress().getAddress())
                .append(", message:").append(e.getMessage()).append("\n");
    }

    public void acceptDNSCheckIOException(DNSCheckIOException e) {
        buffer.append("Unexpected error: ").append(e.getMessage()).append("\n");
    }


    public void acceptRadicalAlterationException(RadicalAlterationCheckException e) {
        buffer.append("All name servers are changed for domain ").append(e.getDomainName());
    }

    public void acceptInternalDNSCheckException(InternalDNSCheckException e) {
        buffer.append("Internal Exception ").append(e.getMessage());
    }

    public void acceptMultipleDNSTechnicalCheckException(MultipleDNSTechnicalCheckException e) {
        for (DNSTechnicalCheckException subEx : e.getExceptions()) subEx.accept(this);
    }

    public void acceptDomainTechnicalCheckException(DomainTechnicalCheckException e) {
    }

    public String getMessages() {
        return buffer.toString();
    }

    private String domainTLDName(String name) {
        return "." + name.toUpperCase();
    }
}
