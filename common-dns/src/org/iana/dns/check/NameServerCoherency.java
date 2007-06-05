package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.exceptions.NameServerCoherencyException;
import org.iana.dns.check.exceptions.UnReachableByUDPException;
import org.xbill.DNS.Record;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */

public class NameServerCoherency implements DNSDomainTechnicalCheck {

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        Set<String> retHostNames = new HashSet<String>();

        for (DNSNameServer ns : nameServers) {
            if (ns.getSOA() == null) throw new UnReachableByUDPException(ns.getName());
            List<Record> nsRecords = Arrays.asList(ns.getSOA().getSectionArray(2));
            for (Record record : nsRecords)
                retHostNames.add(record.getAdditionalName().toString());
        }

        Set<String> domainHostNames = new HashSet<String>();
        for (String name : domain.getNameServerNames())
            domainHostNames.add(name + ".");

        if (!retHostNames.equals(domainHostNames)) throw new NameServerCoherencyException();
    }
}
