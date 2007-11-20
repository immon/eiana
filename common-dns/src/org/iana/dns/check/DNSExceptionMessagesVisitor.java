package org.iana.dns.check;

import org.iana.dns.DNSHost;
import org.iana.dns.check.exceptions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
public class DNSExceptionMessagesVisitor implements DNSTechnicalCheckExceptionVisitor {
    private StringBuffer buffer = new StringBuffer();

    public void acceptDuplicatedASNumberException(DuplicatedASNumberException e) {
        buffer.append("The following name servers have the same AS number (")
                .append(e.getASNumber()).append(") for domain: ")
                .append(e.getDomainName()).append(":\n");
        for (DNSHost host : e.getHosts())
            buffer.append(host.getName()).append("\n");
    }

    public void acceptDuplicatedIPAddressException(DuplicatedIPAddressException e) {
        buffer.append("IP address is duplicated: ")
                .append(e.getIpAddress().getAddress()).append(" for host: ").append(e.getHostName())
                .append(" and domain: ").append(e.getDomainName()).append("\n");
    }

    public void acceptEmptyIPAddressListException(EmptyIPAddressListException e) {
        buffer.append("Host has no IP's: ").append(e.getHostName())
                .append(" for domain: ").append(e.getHostName()).append("\n");
    }

    public void acceptMaximumPayloadSizeExceededException(MaximumPayloadSizeExceededException e) {
        buffer.append("Response estimated size is greater than 512 bytes: ")
                .append(e.getEstimatedSize()).append(" for domain: ")
                .append(e.getDomainName()).append("\n");
    }

    public void acceptNameServerCoherencyException(NameServerCoherencyException e) {
        buffer.append("Supplied name servers names don't match names" +
                " returned in SOA for domain: ").append(e.getDomainName());
    }

    public void acceptNoASNumberException(NoASNumberException e) {
        buffer.append("There is no AS number for IP address: ")
                .append(e.getIpAddress().getAddress()).append(", host: ")
                .append(e.getHostName()).append(" and domain: ").append(e.getDomainName()).append("\n");
    }

    public void acceptNotEnoughNameServersException(NotEnoughNameServersException e) {
        buffer.append("Number of name servers is lower then requested for domain: ")
                .append(e.getDomainName()).append("\n");
    }

    public void acceptReservedIPv4Exception(ReservedIPv4Exception e) {
        buffer.append("IP address is restricted according to RFC 3330: ")
                .append(e.getIpAddress().getAddress()).append(" for host: ").append(e.getHostName())
                .append(" and domain: ").append(e.getDomainName()).append("\n");
    }

    public void acceptSerialNumberNotEqualException(SerialNumberNotEqualException e) {
        buffer.append("There are name servers with more then one serial number for domain: ")
                .append(e.getDomainName()).append(":\n");
        for (Map.Entry<Long, List<DNSHost>> entry : e.getSerialsMap().entrySet()) {
            buffer.append("serial number: ").append(entry.getKey()).append("\n");
            for (DNSHost host : entry.getValue())
                buffer.append("\thost: ").append(host.getName()).append("\n");
        }
    }

    public void acceptNameServerIPAddressesNotEqualException(NameServerIPAddressesNotEqualException e) {
        buffer.append("Current NS IP addresses and retrived from SOA record don't match for host: ")
                .append(e.getHostName()).append("\n");
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
                .append(" for domain: ").append(e.getDomainName()).append("\n");
    }

    public void acceptWhoIsIOException(WhoIsIOException e) {
        buffer.append("Unexpected error checking WhoIs, host: ").append(e.getHostName())
                .append(", IP: ").append(e.getIpAddress().getAddress()).append(", stack trace:\n");
        StringWriter stringWritter = new StringWriter();
        PrintWriter printWritter = new PrintWriter(stringWritter, true);
        e.printStackTrace(printWritter);
        printWritter.flush();
        stringWritter.flush();
        buffer.append(stringWritter.toString()).append("\n");
    }

    public void acceptDNSCheckIOException(DNSCheckIOException e) {
        buffer.append("Unexpected error: ").append(e.getMessage())
                .append(", stack trace:\n");
        StringWriter stringWritter = new StringWriter();
        PrintWriter printWritter = new PrintWriter(stringWritter, true);
        e.printStackTrace(printWritter);
        printWritter.flush();
        stringWritter.flush();
        buffer.append(stringWritter.toString()).append("\n");
    }

    public void acceptMultipleDNSTechnicalCheckException(MultipleDNSTechnicalCheckException e) {
        for (DNSTechnicalCheckException subEx : e.getExceptions()) subEx.accept(this);
    }

    public void acceptDomainTechnicalCheckException(DomainTechnicalCheckException e) {
    }

    public String getMessages() {
        return buffer.toString();
    }
}
