package org.iana.rzm.trans.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSDomainTechnicalCheck;
import org.iana.dns.check.DNSNameServer;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.dns.check.exceptions.RadicalAlterationCheckException;
import org.iana.objectdiff.*;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class RadicalAlterationCheck implements DNSDomainTechnicalCheck {

    DomainManager domainManager;
    private DiffConfiguration diffConfiguration;

    public RadicalAlterationCheck(DomainManager domainManager, DiffConfiguration diffConfiguration) {
        this.domainManager = domainManager;
        this.diffConfiguration = diffConfiguration;
    }


    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        Domain d = domainManager.get(domain.getName());
        if (d != null) {
            DNSDomain currentDomain = d.toDNSDomain();

            ObjectChange change = (ObjectChange) ChangeDetector.diff(currentDomain, domain, diffConfiguration);

            Set<String> touchedNameServers = new HashSet<String>();
            Map<String, Change> fieldChanges = change.getFieldChanges();
            if (fieldChanges.containsKey("nameServers")) {
                CollectionChange nameServersChange = (CollectionChange) fieldChanges.get("nameServers");
                for (Change c : nameServersChange.getModified())
                    touchedNameServers.add(((ObjectChange) c).getId());
                for (Change c : nameServersChange.getRemoved())
                    touchedNameServers.add(((ObjectChange) c).getId());

                Set<String> currentNameServers = currentDomain.getNameServerNames();
                currentNameServers.removeAll(touchedNameServers);

                if(currentNameServers.isEmpty() && !touchedNameServers.isEmpty())
                    throw new RadicalAlterationCheckException(domain);
            }
        }
    }
}
