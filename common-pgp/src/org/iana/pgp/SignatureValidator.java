package org.iana.pgp;

import java.io.InputStream;
import java.security.PublicKey;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public interface SignatureValidator {
    public boolean validate(InputStream in, String armouredKey) throws SignatureValidatorException;

    public boolean validate(InputStream in, PublicKey key) throws SignatureValidatorException;
}
