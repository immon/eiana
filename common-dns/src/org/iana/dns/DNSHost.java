package org.iana.dns;

import org.iana.dns.validator.InvalidIPAddressException;

import java.util.Set;

/**
 * This interface models a host configured in DNS.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSHost extends DNSObject, Comparable<DNSHost> {

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
    String getFullyQualifiedName();

    /**
     * Returns a set of IP addresses configured for this host.
     *
     * @return a set of IP addresses configured for this host.
     */
    Set<DNSIPAddress> getIPAddresses();

    /**
     * Returns a set of IP addresses (as plain strings) configured for this host.
     * The strings in the returned set are valid IP addresses.
     *
     * @return a set of IP addresses (as plain strings) configured for this host.
     */
    Set<String> getIPAddressesAsStrings();

    /**
     * Determines whether this host has a given IP address configured.
     *
     * @param addr the IP address
     * @return true if this host has this IP address configured; false otherwise.
     */
    boolean hasIPAddress(DNSIPAddress addr);

    /**
     * Determines whether this host has a given IP address configured.
     *
     * @param addr the IP address
     * @return true if this host has this IP address configured; false otherwise.
     */
    boolean hasIPAddress(String addr) throws InvalidIPAddressException;

    /**
     * Determines whether this host is in a given domain.
     *
     * @param domain the domain object
     * @return true if this host is in a given domain; false otherwise.
     */
    boolean isInDomain(DNSDomain domain);
}
