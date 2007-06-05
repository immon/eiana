package org.iana.dns;

/**
 * This interface represents a IPv6 address.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSIPv4Address extends DNSIPAddress {

    /**
     * Determines whether this IP address has been allocated or assigned
     * for special use according to RFC 3330.
     *
     * @return true if this IP address has been allocated or assigned
     *         for special use according to RFC 3330; false otherwise.
     */
    boolean isReserved();

}
