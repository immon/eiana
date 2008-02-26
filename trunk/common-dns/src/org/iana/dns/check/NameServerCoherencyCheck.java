package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.exceptions.NameServerCoherencyException;
import org.xbill.DNS.Record;

import java.util.HashSet;
import java.util.Set;

/**
 * (Test 6)
 * Checks NS names returned by authoritative name server, and compare them to the supplied NS names.
 * These should match.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */

public class NameServerCoherencyCheck implements DNSDomainTechnicalCheck {

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        Set<String> domainNameSeverNames = domain.getNameServerNames();
        for (DNSNameServer ns : nameServers) {
            Set<String> retHostNames = new HashSet<String>();
            for (Record record : ns.getNsRecord()) {
                retHostNames.add(removeLastDot(record.getAdditionalName().toString().toLowerCase()));
            }

            if (!retHostNames.equals(domainNameSeverNames)) throw new NameServerCoherencyException(domain, ns.getHost(), domainNameSeverNames, retHostNames);
        }

        
//        List<Record>list = new ArrayList<Record>();
//        for (DNSNameServer ns : nameServers) {
//            Record[] records =  ns.getNsRecord();
//            if(list.isEmpty()){
//                list.addAll(Arrays.asList(records));
//            }else{
//                for (Record record : records) {
//                    if(!list.contains(record)){
//                        throw new NameServerCoherencyException(domain);
//                    }
//                }
//            }
//        }

    }


    public String removeLastDot(String name) {
        if (name.endsWith("."))
            return name.substring(0, name.length() - 1);
        else
            return name;
    }
}
