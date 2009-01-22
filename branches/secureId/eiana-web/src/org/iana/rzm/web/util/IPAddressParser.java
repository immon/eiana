package org.iana.rzm.web.util;

public class IPAddressParser {

    public static final int BYTES_PER_QUAD = 4;
    public static final int BITS_PER_QUAD = 32;

    public static short[] parseIpAddressToNumbers(String addrString) {
        return asBytes(addrString);
    }

    public static short[] parseIpAddressToNumbers(String addrString, int significantBits) {
        return asBytes(addrString, significantBits);
    }

    private static short[] asBytes(String addrString) {
        return asBytes(addrString, BITS_PER_QUAD);
    }

    private static short[] asBytes(String addrString, int significantBits) {
        short[] bytes = new short[BYTES_PER_QUAD];
        String[] parts = addrString.split("\\.");

        if (parts.length != BYTES_PER_QUAD)
            throw new RuntimeException("Invalid Ip Address " + addrString);

        for (int i = 0; i < parts.length; i++) {
            try {
                bytes[i] = Short.parseShort(parts[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Ip Address " + addrString);
            }
            if (bytes[i] < 0 || bytes[i] > 255)
                throw new IllegalArgumentException("Invalid Ip Address "  + addrString);
        }

        clearInsignificant(significantBits, bytes);

        return bytes;
    }

    private static void clearInsignificant(int significantBits, short[] bytes) {
        clearInsignificantBytes(significantBits, bytes);
        clearInsignificantBitsInLastSignificantByte(significantBits, bytes);
    }

    private static void clearInsignificantBytes(int significantBits, short[] bytes) {
        int firstByteToClear;
        if (significantBits % 8 == 0) {
            firstByteToClear = significantBits / 8;
        } else {
            firstByteToClear = (significantBits / 8) + 1;
        }
        for (int j = firstByteToClear; j < BYTES_PER_QUAD; j++) {
            bytes[j] = 0;
        }
    }

    private static void clearInsignificantBitsInLastSignificantByte(int significantBits, short[] bytes) {
        int significantBytes = significantBits / 8;
        if (significantBytes * 8 != significantBits) {
            int byteToMask = significantBits / 8;
            bytes[byteToMask] =
            (short) (bytes[byteToMask] & (insignificantBitsMask(significantBytes, significantBits)));
        }
    }

    private static int insignificantBitsMask(int significantBytes, int significantBits) {
        return 255 << 8 - (significantBits - (significantBytes * 8));
    }

    public static String getLongAsQuad(long ip) {
        short[] shorts = longAsShorts(ip);
        return asQuadString(shorts[0], shorts[1], shorts[2], shorts[3]);
    }

    public static short[] longAsShorts(long ip) {
        short byte1 = (short) ((ip >> 24) & 0xFF);
        short byte2 = (short) ((ip >> 16) & 0xFF);
        short byte3 = (short) ((ip >> 8) & 0xFF);
        short byte4 = (short) ((ip & 0xFF));
        return new short[] {byte1, byte2, byte3, byte4};
    }



    public static String asQuadString(byte[] bytes) {
        return asQuadString(asUnsigned(bytes[0]), asUnsigned(bytes[1]), asUnsigned(bytes[2]), asUnsigned(bytes[3]));
    }

    static short asUnsigned(byte aByte) {
        if (aByte >= 0) {
            return aByte;
        } else {
            return (short) (0x80 + (aByte & 0x7f));
        }
    }

    protected static String asQuadString(short[] bytes) {
        return asQuadString(bytes[0], bytes[1], bytes[2], bytes[3]);
    }

    private static String asQuadString(short byte1, short byte2, short byte3, short byte4) {
        return String.valueOf(byte1) + "." + String.valueOf(byte2) + "." + String.valueOf(byte3) + "." + String.valueOf(
            byte4);
    }



    public static boolean validIpStucture(String ptrName, String revZoneName) {
        String[] ptrStrings = ptrName.split("\\.");
        String[] zoneStrings = revZoneName.split("\\.");
        return ptrStrings.length+zoneStrings.length >= 4;
    }


    public static boolean validIpStructure(String proposedIp) {
        try {
            parseIpAddressToNumbers(proposedIp);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
