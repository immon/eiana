package org.iana.rzm.trans.check;

import org.iana.dns.check.exceptions.RadicalAlterationCheckException;
import org.iana.rzm.domain.Domain;

/**
 * @author Piotr Tkaczyk
 */
public interface RadicalAlteration {

    void check(Domain modifiedDomain) throws RadicalAlterationCheckException;
}
