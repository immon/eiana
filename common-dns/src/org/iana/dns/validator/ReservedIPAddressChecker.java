package org.iana.dns.validator;

import org.iana.dns.DNSIPv4Address;
import org.iana.dns.obj.DNSIPv4AddressImpl;

/**
 * @author Piotr Tkaczyk
 */
public class ReservedIPAddressChecker {

    public static boolean check(String address) {
        return check(new DNSIPv4AddressImpl(address));
    }

    public static boolean check(DNSIPv4Address address) {
        int[] octets = address.getInts();
        // 224.0.0.0/4; 240.0.0.0/4
        if (octets[0] >= 224) return true;
        switch (octets[0]) {
            // 0.0.0.0/8
            case 0:
                return true;
                // 10.0.0.0/8
            case 10:
                return true;
                // 14.0.0.0/8
            case 14:
                return true;
                // 24.0.0.0/8
            case 24:
                return true;
                // 39.0.0.0/8
            case 39:
                return true;
                // 127.0.0.0/8
            case 127:
                return true;
            case 128: // 128.0.0.0/16
                if (octets[1] == 0) return true;
                break;
            case 169: // 169.254.0.0/16
                if (octets[1] == 254) return true;
                break;
            case 172: // 172.16.0.0/12
                if ((octets[1] >= 16) && (octets[1] <= 31)) return true;
                break;
            case 191: // 191.255.0.0/16
                if (octets[1] == 255) return true;
                break;
            case 192: // 192.0.0.0/24; 192.0.2.0/24
                if (octets[1] == 0)
                    if ((octets[2] == 0) || (octets[2] == 2)) return true;
                // 192.88.99.0/24
                if ((octets[1] == 88) && (octets[2] == 99)) return true;
                // 192.168.0.0/16
                if (octets[1] == 168) return true;
                break;
            case 198: // 198.18.0.0/15
                if ((octets[1] == 18) || (octets[1] == 19)) return true;
                break;
            case 223: // 223.255.255.0/24
                if ((octets[1] == 255) && (octets[2] == 255)) return true;
                break;
        }
        return false;
    }
}
