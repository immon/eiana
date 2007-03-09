/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.domain;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;


//for test purpose only IPAddress constructor is protected
public class IPAddressFactory {

    public static IPAddress getIPv4Address(String address) throws InvalidIPAddressException {
        return new IPv4Address(address);
    }
    public static IPAddress getIPv6Address(String address) throws InvalidIPAddressException {
        return new IPv6Address(address);
    }
}
