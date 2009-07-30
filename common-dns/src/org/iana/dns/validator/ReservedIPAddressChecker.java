package org.iana.dns.validator;

import org.apache.log4j.Logger;
import org.iana.dns.DNSIPv4Address;
import org.iana.dns.DNSIPv6Address;
import org.iana.dns.obj.DNSIPv4AddressImpl;
import org.iana.dns.obj.DNSIPv6AddressImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ReservedIPAddressChecker {

    private static Logger logger = Logger.getLogger(ReservedIPAddressChecker.class);

    public static boolean check(String address) {
        return check(new DNSIPv4AddressImpl(address));
    }

    public static boolean check(DNSIPv4Address address) {
        int[] octets = address.getInts();
        // 224.0.0.0/4; 240.0.0.0/4
        if (octets[0] >= 224) return true;
        switch (octets[0]) {
            // 0.0.0.0/8           `
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

    private static Map<DNSIPv6Address, Integer> reservedIPv6;

    static {
        reservedIPv6 = new HashMap<DNSIPv6Address, Integer>();
        reservedIPv6.put(new DNSIPv6AddressImpl("::0"), 128);
        reservedIPv6.put(new DNSIPv6AddressImpl("::1"), 128);
        reservedIPv6.put(new DNSIPv6AddressImpl("2001:2::"), 48);
        reservedIPv6.put(new DNSIPv6AddressImpl("2001:10::"), 28);
        reservedIPv6.put(new DNSIPv6AddressImpl("2001:DB8::"), 32);
        reservedIPv6.put(new DNSIPv6AddressImpl("FC00::"), 7);
        reservedIPv6.put(new DNSIPv6AddressImpl("FE80::"), 10);
        reservedIPv6.put(new DNSIPv6AddressImpl("::FFFF:0:0"), 96);
        reservedIPv6.put(new DNSIPv6AddressImpl("2001::"), 32);
        reservedIPv6.put(new DNSIPv6AddressImpl("2002::"), 16);
    }

    private static Map<Integer, String> maskMapIPv6;

    static {
        maskMapIPv6 = new HashMap<Integer, String>();
        for (Integer mask : Arrays.asList(128, 96, 48, 32, 28, 16, 10, 7)) {
            String maskBin = generateIPv6Mask(mask);
            maskMapIPv6.put(mask, maskBin);
        }
    }




    public static boolean check(DNSIPv6Address address) {

        for (DNSIPv6Address reservedAddress : reservedIPv6.keySet()) {
            if (isReserved(address, reservedAddress, reservedIPv6.get(reservedAddress))) return true;
        }

        return false;
    }

    private static boolean isReserved(DNSIPv6Address address, DNSIPv6Address reservedAddress, int maskValue) {

        String binAddress = convertToBinaryAddress(address);
        String resAddress = convertToBinaryAddress(reservedAddress);
        String mask = maskMapIPv6.get(maskValue);

        for (int i=0; i<mask.length(); i++) {
            if ('1' == mask.charAt(i)) {
                if (binAddress.charAt(i) != resAddress.charAt(i)) {
                    return false;
                }
            }
        }

        logger.debug("Comparing: " + address.getAddress() + " with reserved: " + reservedAddress.getAddress() + " using " + maskValue + " bit mask");
        logger.debug(binAddress);
        logger.debug(resAddress);
        logger.debug(mask);
        logger.debug("Is equal");

        return true;
    }

    private static String generateIPv6Mask(int maskValue) {
        StringBuffer sb = new StringBuffer();

        for (int i=0; i<128; i++) {
            if ((i) < maskValue) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }

        return sb.toString();

    }

    private static String convertToBinaryAddress(DNSIPv6Address address) {
        int[] parts = address.getInts();

        StringBuffer sb = new StringBuffer();
        for (int part : parts) {
            sb.append(getFullBinaryString(part, 16));
        }

        return sb.toString();
    }

    private static String getFullBinaryString(int value, int lenght) {
        String out = Integer.toBinaryString(value);

        StringBuffer leading = new StringBuffer();
        if (out.length() < lenght) {
            for (int i=0; i<(lenght - out.length()); i++) {
                leading.append("0");
            }
        }

        return leading + out;
    }

}
