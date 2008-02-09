package org.iana.rzm.techcheck;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.IPAddress;
import org.iana.rzm.techcheck.exceptions.*;
//import org.iana.dns.validator.SpecialIPAddressChecker;
import org.iana.rzm.techcheck.exceptions.RestrictedIPv4Exception;
import org.xbill.DNS.*;

import java.io.IOException;
import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */

public class TechChecker {

    public static void checkDomain(Domain domain) throws DomainCheckException {

        DomainCheckException domainCheckException = new DomainCheckException(domain.getName());

        List<Host> nameServers = domain.getNameServers();
        if (nameServers.size() < 2) domainCheckException.addException(new NotEnoughHostsException(domain.getName()));
        List<IPAddress> ipAddresses = new ArrayList<IPAddress>();

        long serial = -1;
        for (Host nameServer : nameServers) {

            if (nameServer.getAddresses().isEmpty())
                domainCheckException.addException(new EmptyIPAddressListException(nameServer.getName()));

            for (IPAddress ipAddress : nameServer.getAddresses()) {
                if (ipAddresses.contains(ipAddress))
                    domainCheckException.addException(new DuplicatedIPAddressException(nameServer.getName(), ipAddress.getAddress()));
                try {
                    if (ipAddress.getType().equals(IPAddress.Type.IPv4)) {

//                        if (SpecialIPAddressChecker.isAllocatedForSpecialUse(ipAddress.getAddress()))
                            throw new RestrictedIPv4Exception(ipAddress.getAddress());
                    }
                } catch (RestrictedIPv4Exception e) {
                    domainCheckException.addException(new RestrictedIPv4Exception(nameServer.getName(), ipAddress.toString()));
                }
            }

            ipAddresses.addAll(nameServer.getAddresses());

            try {

                checkReachabilityAndAuthority(domain, nameServer, false);  //by UDP
                checkReachabilityAndAuthority(domain, nameServer, true);   //by TCP
                checkNameServerCoherency(domain, nameServer);
                checkGlueNameServerCoherency(domain, nameServer);
                serial = checkSerialNumber(domain, nameServer, serial);

            } catch (DomainException e) {
                domainCheckException.addException(e);
            }
        }

        if (!domainCheckException.isEmpty()) throw domainCheckException;
    }

    private static void checkReachabilityAndAuthority(Domain domain, Host host, boolean byTCP) throws UnknownHostException, SendNSQueryException, NoAuthoritativeNameServerException {
        try {
            String domainName = domain.getName();
            domainName = (domainName.endsWith(".")) ? domainName : domainName + ".";
            Record question = Record.newRecord(new Name(domainName), Type.SOA, DClass.IN);
            Message query = Message.newQuery(question);
            Resolver resolver = new SimpleResolver(host.getName());
            resolver.setTCP(byTCP);
            Message response = resolver.send(query);

            if ((response == null) || (response.getHeader() == null)) throw new UnknownHostException(host.getName());
            if (!response.getHeader().getFlag(Flags.AA)) throw new NoAuthoritativeNameServerException(host.getName());

        } catch (TextParseException e) {
            // domain name can't be wrong here
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(host.getName(), "host unreachable");
        } catch (IOException e) {
            throw new SendNSQueryException(host.getName(), "Time Out by " + ((byTCP) ? "TCP" : "UDP"));
        }
    }

    private static long checkSerialNumber(Domain domain, Host host, long serial) throws UnknownHostException, SendNSQueryException, SerialNumberNotEqualException {
        long retSerial = serial;
        try {
            String domainName = domain.getName();
            domainName = (domainName.endsWith(".")) ? domainName : domainName + ".";
            Record question = Record.newRecord(new Name(domainName), Type.SOA, DClass.IN);
            Message query = Message.newQuery(question);
            Message response = new SimpleResolver(host.getName()).send(query);

            if ((response == null) || (response.getHeader() == null)) throw new UnknownHostException(host.getName());
            if ((response.getSectionArray(1) == null) || (response.getSectionArray(1)[0] == null) ||
                    !(response.getSectionArray(1)[0] instanceof SOARecord))
                throw new UnknownHostException(host.getName());

            retSerial = ((SOARecord) response.getSectionArray(1)[0]).getSerial();
            if ((serial != -1) && (serial != retSerial))
                throw new SerialNumberNotEqualException(host.getName(), String.valueOf(retSerial));

        } catch (TextParseException e) {
            // domain name can't be wrong here
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(host.getName(), "host unreachable");
        } catch (IOException e) {
            throw new SendNSQueryException(host.getName(), "Time Out");
        }

        return retSerial;
    }

    private static void checkGlueNameServerCoherency(Domain domain, Host host) throws HostIPSetNotEqualException, UnknownHostException, SendNSQueryException {
        try {
            String domainName = domain.getName();
            domainName = (domainName.endsWith(".")) ? domainName : domainName + ".";
            Record question = Record.newRecord(new Name(domainName), Type.SOA, DClass.IN);
            Message query = Message.newQuery(question);
            Message response = new SimpleResolver(host.getName()).send(query);

            if ((response == null) || (response.getSectionArray(3) == null))
                throw new UnknownHostException(host.getName());

            List<Record> nsRecords = Arrays.asList(response.getSectionArray(3));
            Set<IPAddress> retIpAddresses = new HashSet<IPAddress>();
            for (Record record : nsRecords) {
                String address = retrieveIPAddress(record, host.getName());
                if (address != null) retIpAddresses.add(IPAddress.createIPAddress(address));
            }
            if (!retIpAddresses.equals(host.getAddresses()))
                throw new HostIPSetNotEqualException(host.getName());

        } catch (TextParseException e) {
            // domain name can't be wrong here
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(host.getName(), "host unreachable");
        } catch (IOException e) {
            throw new SendNSQueryException(host.getName(), "Time Out");
        }
    }

    private static void checkNameServerCoherency(Domain domain, Host host) throws NSRecordNotEqualException, UnknownHostException, SendNSQueryException {
        try {
            String domainName = domain.getName();
            domainName = (domainName.endsWith(".")) ? domainName : domainName + ".";
            Record question = Record.newRecord(new Name(domainName), Type.NS, DClass.IN);
            Message query = Message.newQuery(question);
            Message response = new SimpleResolver(host.getName()).send(query);

            if ((response == null) || (response.getSectionArray(3) == null))
                throw new UnknownHostException(host.getName());


            List<Record> nsRecords = Arrays.asList(response.getSectionArray(3));
            Set<String> retHostNames = new HashSet<String>();

            for (Record record : nsRecords)
                retHostNames.add(record.getName().toString());

            Set<String> domainHostNames = new HashSet<String>();
            for (Host ns : domain.getNameServers())
                domainHostNames.add((ns.getName().endsWith("\\.")) ? ns.getName() : ns.getName() + ".");

            if (!retHostNames.equals(domainHostNames)) throw new NSRecordNotEqualException(host.getName());

        } catch (TextParseException e) {
            // domain name can't be wrong here
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(host.getName(), "host unreachable");
        } catch (IOException e) {
            throw new SendNSQueryException(host.getName(), "Time Out");
        }
    }

    private static String retrieveIPAddress(Record record, String hostName) {
        String fullHostName = hostName + ((hostName.endsWith(".")) ? "" : ".");
        fullHostName = fullHostName.toLowerCase(Locale.ENGLISH);
        if (record instanceof ARecord) {
            ARecord aRecord = (ARecord) record;
            if (aRecord.getName().toString().toLowerCase(Locale.ENGLISH).equals(fullHostName)) {
                String ipAddress = aRecord.getAddress().toString();
                return ipAddress.replaceFirst("/", "");
            }
        }
        if (record instanceof AAAARecord) {
            AAAARecord a4Record = (AAAARecord) record;
            if (a4Record.getName().toString().toLowerCase(Locale.ENGLISH).equals(fullHostName)) {
                String ipAddress = a4Record.getAddress().toString();
                return ipAddress.replaceFirst("/", "");
            }
        }
        return null;
    }
}
