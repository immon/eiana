package org.iana.dns.check;

import org.iana.dns.*;
import org.iana.dns.check.exceptions.*;
import org.xbill.DNS.*;

import java.util.*;

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
        Set<String> retHostNames = new HashSet<String>();

        //for (DNSNameServer ns : nameServers) {
        //    for (Record record : ns.getAuthoritySection())
        //        retHostNames.add(removeLastDot(record.getAdditionalName().toString().toLowerCase()));
        //}
        //
        //if (!retHostNames.equals(domain.getNameServerNames())) throw new NameServerCoherencyException(domain);

        List<Record>list = new ArrayList<Record>();
        for (DNSNameServer ns : nameServers) {
            Record[] records =  ns.getNsRecord();
            if(list.isEmpty()){
                list.addAll(Arrays.asList(records));
            }else{
                for (Record record : records) {
                    if(!list.contains(record)){
                        throw new NameServerCoherencyException(domain);
                    }
                }
            }
        }

    }



    public String removeLastDot(String name) {
        if (name.endsWith("."))
            return name.substring(0, name.length() - 1);
        else
            return name;
    }
}
