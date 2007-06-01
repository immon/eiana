package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSTechnicalCheck {

    private List<DNSDomainTechnicalCheck> domainChecks;
    private List<DNSNameServerTechnicalCheck> nameServerChecks;

    public void setDomainChecks(List<DNSDomainTechnicalCheck> domainChecks) {
        this.domainChecks = domainChecks;
    }

    public void setNameServerChecks(List<DNSNameServerTechnicalCheck> nameServerChecks) {
        this.nameServerChecks = nameServerChecks;
    }

    public void check(DNSDomain domain) throws DNSTechnicalCheckException {
        if (domain == null) throw new IllegalArgumentException("null domain");
        if (!isEmpty(domainChecks) || !isEmpty(nameServerChecks)) {
            Set<DNSNameServer> nameServers = new HashSet<DNSNameServer>();
            for (DNSHost host : domain.getNameServers()) {
                nameServers.add(new DNSNameServer(domain, host));
            }

            MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();
            checkDomain(domain, nameServers, e);
            for (DNSNameServer ns : nameServers) {
                checkNameServer(ns, e);
            }
        }
    }

    private void checkDomain(DNSDomain domain, Set<DNSNameServer> nameServers, MultipleDNSTechnicalCheckException error) {
        if (!isEmpty(domainChecks)) {
            for (DNSDomainTechnicalCheck check : domainChecks) {
                try {
                    check.check(domain, nameServers);
                } catch (MultipleDNSTechnicalCheckException e) {
                    error.addExceptions(e.getExceptions());
                } catch (DNSTechnicalCheckException e) {
                    error.addException(e);
                }
            }
        }
    }

    private void checkNameServer(DNSNameServer nameServer, MultipleDNSTechnicalCheckException error) {
        if (!isEmpty(domainChecks)) {
            for (DNSNameServerTechnicalCheck check : nameServerChecks) {
                try {
                    check.check(nameServer);
                } catch (MultipleDNSTechnicalCheckException e) {
                    error.addExceptions(e.getExceptions());
                } catch (DNSTechnicalCheckException e) {
                    error.addException(e);
                }
            }
        }
    }

    private boolean isEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }
}
