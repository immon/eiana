package org.iana.dns.check.remote;

import org.iana.dns.check.*;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * The default implementation of RPCTechnicalCheck interface.
 *
 * @author Patrycja Wegrzynowicz
 */
public class RPCTechnicalCheckImpl implements RPCTechnicalCheck {

    public void check(String domainName, String[] nameServers, String[][] ipAddresses) throws RemoteException, DNSTechnicalCheckException {
        DNSDomain domain = toDomain(domainName, nameServers, ipAddresses);
        DNSTechnicalCheck check = new DNSTechnicalCheck();
        // todo: make technical checks configurable
        List<DNSNameServerTechnicalCheck> nsChecks = new ArrayList<DNSNameServerTechnicalCheck>();
        nsChecks.add(new NameServerReachabilityCheck());
        nsChecks.add(new NameServerAuthorityCheck());
        nsChecks.add(new GlueCoherencyCheck());
        check.setDomainChecks(Arrays.asList(new DNSDomainTechnicalCheck[]{
            new MinimumNameServersAndNoReservedIPsCheck(),
            new MinimumNetworkDiversityCheck(),
            new NameServerCoherencyCheck(),
            new SerialNumberCoherencyCheck()
        }));
        check.setNameServerChecks(Arrays.asList(new DNSNameServerTechnicalCheck[]{
            new NameServerReachabilityCheck(),
            new NameServerAuthorityCheck(),
            new GlueCoherencyCheck()
        }));
        check.check(domain);
    }


    public SOA[] querySOA(String domainName, String[] nameServers, String[][] ipAddresses) throws RemoteException, IllegalArgumentException, InvalidDomainNameException, InvalidIPAddressException {
        DNSDomain domain = toDomain(domainName, nameServers, ipAddresses);
        List<SOA> ret = new ArrayList<SOA>();
        for (DNSHost host : domain.getNameServers()) {
            DNSNameServer ns = new DNSNameServer(domain, host);
            ret.add(new SOA(host.getName(), ns.getSOAByUDP(), true));
            ret.add(new SOA(host.getName(), ns.getSOAByTCP(), false));
        }
        return ret.toArray(new SOA[0]);
    }

    private DNSDomain toDomain(String domainName, String[] nameServers, String[][] ipAddresses) throws IllegalArgumentException, InvalidDomainNameException, InvalidIPAddressException {
        if (nameServers.length != ipAddresses.length) throw new IllegalArgumentException("ip addresses array does not correspont to name servers array (different length)");
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
