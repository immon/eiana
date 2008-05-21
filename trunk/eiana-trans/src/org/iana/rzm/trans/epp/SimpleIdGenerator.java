package org.iana.rzm.trans.epp;

import java.rmi.server.UID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Re-used jlaszk code.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class SimpleIdGenerator implements EPPIdGenerator {

    public String id() {
        return encode(new UID().toString());
    }

    private String encode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte encoded[] = md.digest(input.getBytes());
            StringBuffer output = new StringBuffer();
            for (byte anEncoded : encoded)
                output.append(Integer.toHexString((0xff & anEncoded)));
            return output.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
