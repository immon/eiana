package org.iana.dns.obj;

import static org.iana.dns.DNSIPAddress.Type.IPv4;
import org.iana.dns.DNSIPv4Address;
import org.iana.dns.validator.IPAddressValidator;
import org.iana.dns.validator.InvalidIPv4AddressException;
import org.iana.dns.validator.SpecialIPAddressChecker;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSIPv4AddressImpl extends DNSIPAddressImpl implements DNSIPv4Address {

    public DNSIPv4AddressImpl(String address) throws InvalidIPv4AddressException {
        super(address);
        IPAddressValidator.getInstance().validateIPv4(address);
    }

    public boolean isAllocatedForSpecialUse() {
        return SpecialIPAddressChecker.isAllocatedForSpecialUse(this);
    }

    public String[] getParts() {
        return address.split("\\.");
    }

    protected int toInt(String s) {
        return Integer.parseInt(s);
    }

    public Type getType() {
        return IPv4;
    }

    public String getCompressedAddress() {
        return getAddress();
    }
}
