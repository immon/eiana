package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * It performs a series of pre-configured technical checks for a given domain. These
 * checks include domain checks (i.e. the checks conducted in a context of a domain as a whole)
 * and name server checks (i.e. the checks conducted in a context of a single name server).
 * All domain checks are conducted prior to the name server checks. A failed check (i.e. exception thrown) does not
 * stop processing, instead an exception thrown during processing is added to a MultipleDNSTechnicalCheckException instance
 * and this composite exception is thrown after all checks are completed.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class DNSTechnicalCheck {

    private List<DNSDomainTechnicalCheck> domainChecks;
    private List<DNSNameServerTechnicalCheck> nameServerChecks;
    private int dnsCheckRetries;

    public void setDomainChecks(List<DNSDomainTechnicalCheck> domainChecks) {
        this.domainChecks = domainChecks;
    }

    public void setNameServerChecks(List<DNSNameServerTechnicalCheck> nameServerChecks) {
        this.nameServerChecks = nameServerChecks;
    }

    public void setDnsCheckRetries(int dnsCheckRetries) {
        this.dnsCheckRetries = dnsCheckRetries;
    }

    public DNSCheckCollectionResult checkWithResult(DNSDomain domain) {
        if (domain == null) throw new IllegalArgumentException("null domain");
        DNSCheckCollectionResult result = new DNSCheckCollectionResult();
        if (!isEmpty(domainChecks) || !isEmpty(nameServerChecks)) {
            Set<DNSNameServer> nameServers = new HashSet<DNSNameServer>();
            for (DNSHost host : domain.getNameServers()) {
                nameServers.add(new DNSNameServer(domain, host, dnsCheckRetries));
            }
            checkDomain(domain, nameServers, result);
            for (DNSNameServer ns : nameServers) {
                checkNameServer(ns, result);
            }
        }

        return result;
    }

    public void check(DNSDomain domain) throws DNSTechnicalCheckException {
        MultipleDNSTechnicalCheckException e = checkWithResult(domain).getException();
        if (!e.isEmpty()) throw e;
    }

    public DNSCheckCollectionResult checkWithResult(DNSDomain domain, Set<DNSNameServer> nameServers) {
        if (domain == null) throw new IllegalArgumentException("null domain");
        if (nameServers == null) throw new IllegalArgumentException("null name servers");
        DNSCheckCollectionResult result = new DNSCheckCollectionResult();
        if (!isEmpty(domainChecks) || !isEmpty(nameServerChecks)) {
            checkDomain(domain, nameServers, result);
            for (DNSNameServer ns : nameServers) {
                checkNameServer(ns, result);
            }
        }

        return result;
    }

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        MultipleDNSTechnicalCheckException e = checkWithResult(domain, nameServers).getException();
        if (!e.isEmpty()) throw e;
    }

    private void checkDomain(DNSDomain domain, Set<DNSNameServer> nameServers, DNSCheckCollectionResult result) {
        if (!isEmpty(domainChecks)) {
            for (DNSDomainTechnicalCheck check : domainChecks) {
                DNSCheckResult r = check.check(domain, nameServers);
                result.addResult(r);
            }
        }
    }

    private void checkNameServer(DNSNameServer nameServer, DNSCheckCollectionResult result) {
        if (!isEmpty(nameServerChecks)) {
            for (DNSNameServerTechnicalCheck check : nameServerChecks) {
                DNSCheckResult r = check.check(nameServer);
                result.addResult(r);
            }
        }
    }

    private boolean isEmpty(Collection<?> col) {
        return col == null || col.isEmpty();
    }
}
