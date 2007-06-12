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
 * All domain checks are conducted prior to name server checks. A failed check (i.e. exception thrown) does not
 * stop processing, instead an exception thrown during processing is added to a MultipleDNSTechnicalCheckException instance. 
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
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
            if (!e.isEmpty()) throw e;
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
        if (!isEmpty(nameServerChecks)) {
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

    private boolean isEmpty(Collection<?> col) {
        return col == null || col.isEmpty();
    }
}
