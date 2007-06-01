package org.iana.dns.validator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class IPAddressValidator {

    public void validateIPv4(String address) throws InvalidIPv4AddressException {
        if (address == null) throw new IllegalArgumentException("empty ipv4 address");
        List<String> pieces = Arrays.asList(address.split("\\."));
        if (pieces.size() != 4) throw new InvalidIPv4AddressException(address);
        for (String piece : pieces) {
            try {
                int num = Integer.parseInt(piece);
                if (num < 0 || num > 255 || !Integer.toString(num).equals(piece)) throw new InvalidIPv4AddressException(address);
            } catch (NumberFormatException e) {
                throw new InvalidIPv4AddressException(address);
            }
        }
    }

    public void validateIPv6(String address) throws InvalidIPv6AddressException {
        if (address == null) throw new IllegalArgumentException("empty ipv6 address");
        try {
            int doubleColonPosition = address.indexOf("::");
            if (doubleColonPosition == -1) {
                List<String> pieces = Arrays.asList(address.split(":"));
                if (pieces.size() == 7) { //possibility of ipv4 on end
                    for (Iterator iterator = pieces.iterator(); iterator.hasNext();) {
                        String piece = (String) iterator.next();
                        if (!validateHEX4(piece)) {
                            if (!iterator.hasNext())
                                validateIPv4(piece);
                            else
                                throw new InvalidIPv6AddressException(address);
                        }
                    }
                } else {
                    if (pieces.size() != 8) throw new InvalidIPv6AddressException(address);
                    for (String piece : pieces)
                        if (!validateHEX4(piece)) throw new InvalidIPv6AddressException(address);
                }
            } else {
                if (address.indexOf("::", doubleColonPosition + 1) != -1) throw new InvalidIPv6AddressException(address);
                List<String> pieces = Arrays.asList(address.split("::"));
                if (pieces.size() != 0)   // if not "::" address
                    for (Iterator piecesIterator = pieces.iterator(); piecesIterator.hasNext();) {
                        List<String> parts = Arrays.asList(((String) piecesIterator.next()).split(":"));
                        for (Iterator partsIterator = parts.iterator(); partsIterator.hasNext();) {
                            String part = (String) partsIterator.next();
                            if (!validateHEX4(part))
                                if (!partsIterator.hasNext() && !piecesIterator.hasNext())
                                    validateIPv4(part);
                                else
                                    throw new InvalidIPv6AddressException(address);
                        }
                    }
            }
        } catch (InvalidIPv4AddressException e) {
            throw new InvalidIPv6AddressException(address);
        }
    }

    private boolean validateHEX4(String hex4) {
        if (hex4.length()>4) return false;
        try {
            if (hex4.length() != 0) Integer.parseInt(hex4, 16);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private IPAddressValidator() {
    }

    private static IPAddressValidator instance = new IPAddressValidator();

    public static IPAddressValidator getInstance() { return instance; }

//    public static void main(String[] args) throws Exception {
//        IPAddressValidator v = IPAddressValidator.getInstance();
//        String[] good_addresses =  {"fe80:0000:0000:0000:1322:33FF:FE44:5566",
//                                    "2001:0000:0:fd:123:ffff:0:0",
//                                    "2001:0db8:0000:0000:0000::1428:57ab",
//                                    "2001:0db8:0:0:0:0:1428:57ab",
//                                    "2001:0db8:0:0::1428:57ab",
//                                    "2001:0db8::1428:57ab",
//                                    "2001:db8::1428:57ab",
//                                    "f::",
//                                    "::f",
//                                    "::f:128",
//                                    "::1.2.255.254",
//                                    "::ffff:0:0",
//                                    "::ffff:0:1.2.3.4",
//                                    "1:2:3:4:5:6:1.2.3.4"};
//        List<String> goodAddresses = Arrays.asList(good_addresses);
//        for (String good : goodAddresses) {
//            v.validateIPv6(good);
//        }
//
//
//        String[] invalid_addresses =   {":::",
//                                        ":f:",
//                                        "2001::0000:0:fd:123:ffff::0:0",
//                                        "1:2:3:4:5:1.2.3.4",
//                                        "2001:0000:0:::123:0",
//                                        "1:2:3:4:5:6:7:1.2.3.4"};
//        List<String> invalidAddresses = Arrays.asList(invalid_addresses);
//        int errorCounter = 0;
//        for (String invalid : invalidAddresses) {
//            try {
//                v.validateIPv6(invalid);
//            } catch (Exception e) {
//                errorCounter++;
//            }
//        }
//        if (errorCounter != invalidAddresses.size())
//            System.out.print("Error. Probably good address in invalid addresses list.");
//    }
}
