package org.iana.rzm.trans.dns;

import org.iana.dns.check.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class DNSTechnicalCheckFactory {
    public static DNSTechnicalCheck getZoneCheck() {
        DNSTechnicalCheck result = new DNSTechnicalCheck();
        result.setDomainChecks(Collections.<DNSDomainTechnicalCheck>singletonList(new NameServerCoherencyCheck()));
        return result;
    }

    public static DNSTechnicalCheck getDomainCheck() {
        DNSTechnicalCheck result = new DNSTechnicalCheck();

        List<DNSDomainTechnicalCheck> domainChecks = new ArrayList<DNSDomainTechnicalCheck>();
        domainChecks.add(new MinimumNameServersAndNoReservedIPsCheck());
        domainChecks.add(new MinimumNetworkDiversityCheck());
        domainChecks.add(new NameServerCoherencyCheck());
        domainChecks.add(new SerialNumberCoherencyCheck());
        domainChecks.add(new MaximumPayloadSizeCheck());
        result.setDomainChecks(domainChecks);

        List<DNSNameServerTechnicalCheck> nsChecks = new ArrayList<DNSNameServerTechnicalCheck>();
        nsChecks.add(new NameServerReachabilityCheck());
        nsChecks.add(new NameServerAuthorityCheck());
        nsChecks.add(new GlueCoherencyCheck());
        result.setNameServerChecks(nsChecks);

        return result;
    }
}
