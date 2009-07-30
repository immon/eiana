package org.iana.dns;

/**
 * This interface models an IP address. 
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSIPAddress extends DNSObject, Comparable<DNSIPAddress> {

    enum Type { IPv4, IPv6 }

    /**
     * Returns the type of this IP address.
     *
     * @return the type of this IP address
     */
    Type getType();

    /**
     * Returns the value of this IP address (in case of IPv6 the uncompressed version is returned).
     *
     * @return the (uncompressed) value of this IP address
     */
    String getAddress();

    /**
     * Returns a compressed version of this IP address. If the address cannot be compressed
     * the address is returned.
     *
     * @return the compressed version of this IP address
     */
    String getCompressedAddress();

    /**
     * Returns an array containing the parts of this IP address (uncompressed). Each part of IP address
     * is a string.
     *
     * @return an array of the parts of this IP address.
     */
    String[] getParts();

    /**
     * Returns an array containing the parts of this IP address (uncompressed). Each part of IP address
     * is an integer.
     *
     * @return an array of the parts of this IP address.
     */
    int[] getInts();

    /**
     * Determines whether this IP address has been allocated or assigned
     * for special use according to RFC 3330.
     *
     * @return true if this IP address has been allocated or assigned
     *         for special use according to RFC 3330; false otherwise.
     */
    boolean isReserved();
}
