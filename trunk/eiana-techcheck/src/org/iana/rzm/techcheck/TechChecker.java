package org.iana.rzm.techcheck;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.IPAddress;
import org.iana.rzm.techcheck.exceptions.*;
import org.xbill.DNS.*;

import java.util.*;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 * @author: Piotr Tkaczyk
 */

public class TechChecker {

    public static void checkDomain(Domain domain) throws DomainCheckException {
        checkNameServers(domain);
    }

    private static void checkNameServers(Domain domain) throws DomainCheckException {
        List<Host> nameServers = domain.getNameServers();
        if (nameServers.size() < 2) throw new NotEnoughHostsException();
        List<IPAddress> ipAddresses = new ArrayList<IPAddress>();
        for (Host nameServer : nameServers) {
            if (nameServer.getAddresses().isEmpty()) throw new EmptyIPAddressListException();
            for (IPAddress ipAddress : nameServer.getAddresses())
                if (ipAddresses.contains(ipAddress)) throw new DuplicatedIPAddressException(ipAddress.getAddress());

            ipAddresses.addAll(nameServer.getAddresses());
        }
        for (Host nameServer : nameServers) {
            checkNameServerReachabilityAndAuthority(domain, nameServer);
            checkNameServerCoherency(domain, nameServer);
        }
    }

    private static void checkNameServerReachabilityAndAuthority(Domain domain, Host host) throws NoAuthoritativeNameServerException, UnknownNameServerException, SendNSQueryException {
        try {
            String domainName = domain.getName();
            domainName =  (domainName.endsWith("."))? domainName : domainName + ".";
            Record question = Record.newRecord(new Name(domainName), Type.SOA, DClass.IN);
            Message query = Message.newQuery(question);
            Message response = new SimpleResolver(host.getName()).send(query);

            if ((response == null) || (response.getHeader() == null) ||
                    (!response.getHeader().getFlag(Flags.AA))) throw new NoAuthoritativeNameServerException(host.getName());

        } catch (TextParseException e) {
            // domain name can't be wrong here
        } catch (UnknownHostException e) {
            throw new UnknownNameServerException(host.getName());
        } catch (IOException e) {
            throw new SendNSQueryException(e);
        }
    }

    private static void checkNameServerCoherency(Domain domain, Host host) throws HostIPSetNotEqualException, UnknownNameServerException, SendNSQueryException {
        try {
            String domainName = domain.getName();
            domainName =  (domainName.endsWith("."))? domainName : domainName + ".";
            Record question = Record.newRecord(new Name(domainName), Type.NS, DClass.IN);
            Message query = Message.newQuery(question);
            Message response = new SimpleResolver(host.getName()).send(query);
            
            List<Record> nsRecords = Arrays.asList(response.getSectionArray(3));
            Set<IPAddress> retIpAddresses = new HashSet<IPAddress>();
            for (Record record : nsRecords) {
                String address = retrieveIPAddress(record, host.getName());
                if (address != null) retIpAddresses.add(IPAddress.createIPAddress(address));
            }
            if (!retIpAddresses.equals(host.getAddresses()))
                throw new HostIPSetNotEqualException();      


        } catch (TextParseException e) {
            // domain name can't be wrong here
        } catch (UnknownHostException e) {
            throw new UnknownNameServerException(host.getName());
        } catch (IOException e) {
            throw new SendNSQueryException(e);
        }
    }

    private static String retrieveIPAddress(Record record, String hostName) {
        String fullHostName = hostName + ((hostName.endsWith("."))? "" : ".");
        fullHostName = fullHostName.toLowerCase(Locale.ENGLISH);
        if (record instanceof ARecord){
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
