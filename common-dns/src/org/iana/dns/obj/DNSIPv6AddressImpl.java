package org.iana.dns.obj;

import org.iana.dns.DNSIPv4Address;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.DNSIPv6Address;
import org.iana.dns.validator.SpecialIPAddressChecker;
import org.iana.dns.validator.IPAddressValidator;
import org.iana.dns.validator.InvalidIPv6AddressException;
import static org.iana.dns.DNSIPAddress.Type.*;

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
        return Integer.parseInt(s);
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
        addr = addr.toLowerCase();
        IPAddressValidator.getInstance().validateIPv6(addr);
        if (isCompressed(addr)) {
            String[] parts = getParts(addr);
            String uncompressed = ":";
            for (int i = 0; i < 8-parts.length; --i) uncompressed += "0:";
            addr = addr.replaceFirst("::", uncompressed);
        }
        return addr;
    }

    private static boolean isCompressed(String addr) {
        return addr.indexOf("::") >= 0;
    }

    private static String[] getParts(String addr) {
        return addr.split(":");
    }
}
