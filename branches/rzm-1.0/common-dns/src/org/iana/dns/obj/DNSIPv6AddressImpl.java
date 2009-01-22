package org.iana.dns.obj;

import static org.iana.dns.DNSIPAddress.Type.IPv6;
import org.iana.dns.DNSIPv6Address;
import org.iana.dns.DNSVisitor;
import org.iana.dns.validator.IPAddressValidator;
import org.iana.dns.validator.InvalidIPv6AddressException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSIPv6AddressImpl extends DNSIPAddressImpl implements DNSIPv6Address {

    public DNSIPv6AddressImpl(String address) throws InvalidIPv6AddressException {
        super(normalize(address));
    }

    public boolean isAllocatedForSpecialUse() {
        return false;
    }

    protected int toInt(String s) {
        try {
            return Integer.parseInt(s, 16);
        } catch (NumberFormatException e) {
            // in case of IPv6 combined with IPv4 NumberFormatException is thrown
            return -1;
        }
    }

    public Type getType() {
        return IPv6;
    }

    public String[] getParts() {
        return getParts(address);
    }

    public String getCompressedAddress() {
        // todo: compression
        return getAddress();
    }

    private static String normalize(String addr) throws InvalidIPv6AddressException {
        if (addr == null) throw new IllegalArgumentException("null address");
        addr = addr.toUpperCase();
        IPAddressValidator.getInstance().validateIPv6(addr);
        return isCompressed(addr) ? uncompress(addr) : addr;
    }

    private static boolean isCompressed(String addr) {
        return addr.indexOf("::") >= 0;
    }

    private static String uncompress(String addr) {
        boolean start = addr.startsWith(":");
        boolean end = addr.endsWith(":");
        int missing = 8 - colons(addr);
        if (start || end) ++missing;
        StringBuffer zeros = new StringBuffer(":");
        while (missing-- > 0) zeros.append("0:");
        if (start) zeros.deleteCharAt(0);
        if (end) zeros.deleteCharAt(zeros.length() - 1);
        return addr.replaceFirst("::", zeros.toString());
    }

    private static String[] getParts(String addr) {
        return addr.split(":");
    }

    private static int colons(String addr) {
        int ret = 0;
        for (char c : addr.toCharArray()) {
            if (c == ':') ++ret;
        }
        return ret;
    }

    public void accept(DNSVisitor visitor) {
        visitor.visitIPv6Address(this);
    }
}
