package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * @author Piotr Tkaczyk
 */
public class RadicalAlterationCheckException extends DomainTechnicalCheckException {

    public RadicalAlterationCheckException(DNSDomain domain) {
        super(domain, null);
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptRadicalAlterationException(this);
    }
}
