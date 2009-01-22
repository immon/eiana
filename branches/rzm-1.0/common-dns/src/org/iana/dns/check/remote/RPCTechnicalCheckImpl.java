package org.iana.dns.check.remote;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSNameServer;
import org.iana.dns.check.exceptions.NameServerUnreachableException;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * The default implementation of RPCTechnicalCheck interface.
 *
 * @author Patrycja Wegrzynowicz
 */
public class RPCTechnicalCheckImpl implements RPCTechnicalCheck {

    private static final int DEFAULT_NUMBERS_OF_RETRIES = 3;

    public SOA[] querySOA(String domainName, String[] nameServers, String[][] ipAddresses) throws RemoteException, RPCTechnicalCheckException {
        return querySOA(domainName, nameServers, ipAddresses, DEFAULT_NUMBERS_OF_RETRIES);
    }

    public SOA[] querySOA(String domainName, String[] nameServers, String[][] ipAddresses, int dnsCheckRetries) throws RemoteException, RPCTechnicalCheckException {
        try {
            DNSDomain domain = toDomain(domainName, nameServers, ipAddresses);
            List<SOA> ret = new ArrayList<SOA>();
            for (DNSHost host : domain.getNameServers()) {
                DNSNameServer ns = new DNSNameServer(domain, host, dnsCheckRetries);
                ret.add(new SOA(host.getName(), ns.getSOAByUDP(), true));
                ret.add(new SOA(host.getName(), ns.getSOAByTCP(), false));
            }
            return ret.toArray(new SOA[0]);
        } catch (RuntimeException e) {
            throw new RPCTechnicalCheckException(e);
        } catch (NameServerUnreachableException e) {
            throw new RPCTechnicalCheckException(e);
        }
    }

    private DNSDomain toDomain(String domainName, String[] nameServers, String[][] ipAddresses) throws IllegalArgumentException, InvalidDomainNameException, InvalidIPAddressException {
        if (nameServers.length != ipAddresses.length)
            throw new IllegalArgumentException("ip addresses array does not correspont to name servers array (different length)");
        DNSDomainImpl domain = new DNSDomainImpl(domainName);
        for (int i = 0; i < nameServers.length; ++i) {
            DNSHostImpl host = new DNSHostImpl(nameServers[i]);
            for (String addr : ipAddresses[i]) {
                host.addIPAddress(addr);
            }
            domain.addNameServer(host);
        }
        return domain;
    }
}
