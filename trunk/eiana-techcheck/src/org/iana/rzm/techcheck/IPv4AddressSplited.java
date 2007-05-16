package org.iana.rzm.techcheck;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author: Piotr Tkaczyk
 */
public class IPv4AddressSplited {

    List<String> pieces;

    public IPv4AddressSplited(String address) {
        pieces = Arrays.asList(address.split("\\."));

    }

    public int getFirstOctet() {
        return Integer.parseInt(pieces.get(0));
    }

    public int getSecondOctet() {
        return Integer.parseInt(pieces.get(1));
    }

    public int getThirdOctet() {
        return Integer.parseInt(pieces.get(2));
    }

    public int getFourthOctet() {
        return Integer.parseInt(pieces.get(3));
    }

    public String toString() {
        StringBuffer ret = new StringBuffer();
        for (Iterator iterator = pieces.iterator(); iterator.hasNext();) {
            ret.append(iterator.next());
            if (iterator.hasNext()) ret.append(".");
        }
        return ret.toString();
    }
}
