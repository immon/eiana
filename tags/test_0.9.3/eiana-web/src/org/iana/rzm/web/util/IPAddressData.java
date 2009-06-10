package org.iana.rzm.web.util;

import java.util.*;


public class IPAddressData {

    private short[] bytes = new short[IPAddressParser.BYTES_PER_QUAD];

    public IPAddressData(long address) {
        bytes = IPAddressParser.longAsShorts(address);
    }

    public IPAddressData(String address) {
        this(address, 32);
    }

    public IPAddressData(String address, int length) {
        if (length < 0 || length > IPAddressParser.BITS_PER_QUAD) {
            throw new IllegalArgumentException("Invalid Network Length");
        }
        bytes = IPAddressParser.parseIpAddressToNumbers(address, length);
    }

    public String getAddress() {
        return IPAddressParser.asQuadString(bytes);
    }

    public short[] getBytes() {
        return bytes;
    }

    public long getAddressAsLong() {
        long addr = ((long) bytes[3]) & 0xFF;
        addr |= ((((long) bytes[2]) << 8) & 0xFF00);
        addr |= ((((long) bytes[1]) << 16) & 0xFF0000);
        addr |= ((((long) bytes[0]) << 24) & 0xFF000000);
        return addr;
    }

    public String toString() {
        return getAddress();
    }

    public int compareTo(Object o) {
        return new Long(getAddressAsLong()).compareTo(((IPAddressData) o).getAddressAsLong());
    }

    public boolean greaterThan(IPAddressData other) {
        return compareTo(other) > 0;
    }

    public boolean greaterThanOrEqual(IPAddressData other){
        return compareTo(other) >= 0;
    }

    public boolean lessThan(IPAddressData other) {
        return compareTo(other) < 0;
    }

    public boolean lessThanOrEqual(IPAddressData other){
        return compareTo(other) <= 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IPAddressData)) {
            return false;
        }

        final IPAddressData ipAddressData = (IPAddressData) o;

        if (!Arrays.equals(bytes, ipAddressData.bytes)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return 0;
    }


}
