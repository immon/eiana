package org.iana.rzm.techcheck;

import org.iana.rzm.techcheck.exceptions.RestrictedIPv4Exception;

/**
 * @author: Piotr Tkaczyk
 */
public class RestrictedIPv4Checker {

    public static void check(String ipAaddress) throws RestrictedIPv4Exception{
        IPv4AddressSplited address = new IPv4AddressSplited(ipAaddress);
        // 224.0.0.0/4; 240.0.0.0/4
        if (address.getFirstOctet() >= 224) throw new RestrictedIPv4Exception(address.toString());
        switch(address.getFirstOctet()) {
            // 0.0.0.0/8
            case 0:   throw new RestrictedIPv4Exception(address.toString());
            // 10.0.0.0/8
            case 10:  throw new RestrictedIPv4Exception(address.toString());
            // 14.0.0.0/8
            case 14:  throw new RestrictedIPv4Exception(address.toString());
            // 24.0.0.0/8
            case 24:  throw new RestrictedIPv4Exception(address.toString());
            // 39.0.0.0/8
            case 39:  throw new RestrictedIPv4Exception(address.toString());
            // 127.0.0.0/8
            case 127: throw new RestrictedIPv4Exception(address.toString());
            case 128: // 128.0.0.0/16
                if (address.getSecondOctet() == 0) throw new RestrictedIPv4Exception(address.toString());
            break;
            case 169: // 169.254.0.0/16
                if (address.getSecondOctet() == 254) throw new RestrictedIPv4Exception(address.toString());
            break;
            case 172: // 172.16.0.0/12
                if ((address.getSecondOctet() >= 16) && (address.getSecondOctet() <= 31)) throw new RestrictedIPv4Exception(address.toString());
            break;
            case 191: // 191.255.0.0/16
                if (address.getSecondOctet() == 255) throw new RestrictedIPv4Exception(address.toString());
            break;
            case 192: // 192.0.0.0/24; 192.0.2.0/24
                if (address.getSecondOctet() == 0)
                    if ((address.getThirdOctet() == 0) || (address.getThirdOctet() == 2)) throw new RestrictedIPv4Exception(address.toString());
                // 192.88.99.0/24
                if ((address.getSecondOctet() == 88) && (address.getThirdOctet() == 99)) throw new RestrictedIPv4Exception(address.toString());
                // 192.168.0.0/16
                if (address.getSecondOctet() == 168) throw new RestrictedIPv4Exception(address.toString());
            break;
            case 198: // 198.18.0.0/15
                if ((address.getSecondOctet() == 18) || (address.getSecondOctet() == 19)) throw new RestrictedIPv4Exception(address.toString());
            break;
            case 223: // 223.255.255.0/24
                if ((address.getSecondOctet() == 255) && (address.getThirdOctet() == 255)) throw new RestrictedIPv4Exception(address.toString());
            break;
        }
    }
}
