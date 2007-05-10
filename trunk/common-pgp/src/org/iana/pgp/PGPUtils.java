package org.iana.pgp;

import org.iana.pgp.cryptix.CryptixPGPUtils;

import java.security.PublicKey;

/**
 * @author Jakub Laszkiewicz
 */
public class PGPUtils {
    public static PublicKey toPublicKey(String armouredKey) throws PGPUtilsException {
        try {
            return CryptixPGPUtils.toPublicKey(armouredKey);
        } catch (Exception e) {
            throw new PGPUtilsException("while converting", e);
        }
    }
}
