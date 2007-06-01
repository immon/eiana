package org.iana.dns;

import java.util.Set;

/**
 * This interface models a host configured in DNS.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSHost {

    /**
     * Returns the name of this domain. The returned name is lower-cased and is a valid name according to RFC 1034.
     * Examples: <code>com</code> <code>nask.pl</code>
     *
     * @return the name of this domain.
     */
    String getName();

    /**
     * Returns the name of this domain with a trailing dot. The returned name is lower-cased.
     * Examples: <code>com.</code> <code>nask.pl.</code>
     *
     * @return the name of this domain with a trailing dot.
     */
    String getNameWithDot();

    Set<DNSIPAddress> getIPAddresses();

    Set<String> getIPAddressesAsStrings();

    boolean hasIPAddress(DNSIPAddress addr);

    boolean hasIPAddress(String addr);
}
