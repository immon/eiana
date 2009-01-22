package org.iana.rzm.trans.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSNameServer;
import org.iana.dns.check.DNSTechnicalCheck;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.dns.check.exceptions.RadicalAlterationCheckException;
import org.iana.rzm.domain.Domain;

import java.util.HashSet;

/**
 * @author Piotr Tkaczyk
 */
public class RadicalAlterationImpl implements RadicalAlteration {

    DNSTechnicalCheck technicalCheck;

    public RadicalAlterationImpl(DNSTechnicalCheck technicalCheck) {
        this.technicalCheck = technicalCheck;
    }

    public void check(Domain modifiedDomain) throws RadicalAlterationCheckException {
        DNSDomain modifiedDNSDomain = modifiedDomain.toDNSDomain();
        try {
            technicalCheck.check(modifiedDNSDomain, new HashSet<DNSNameServer>());
        } catch(DNSTechnicalCheckException e) {
            throw new RadicalAlterationCheckException(modifiedDNSDomain);
        }
    }
}
