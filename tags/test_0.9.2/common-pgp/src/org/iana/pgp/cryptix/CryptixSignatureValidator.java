package org.iana.pgp.cryptix;

import cryptix.message.Message;
import cryptix.message.SignedMessage;
import org.iana.pgp.SignatureValidator;
import org.iana.pgp.SignatureValidatorException;

import java.io.InputStream;
import java.security.PublicKey;

/**
 * @author Jakub Laszkiewicz
 */
public class CryptixSignatureValidator implements SignatureValidator {
    static {
        java.security.Security.addProvider(new cryptix.jce.provider.CryptixCrypto());
        java.security.Security.addProvider(new cryptix.openpgp.provider.CryptixOpenPGP());
    }


    public boolean validate(InputStream in, String armouredKey) throws SignatureValidatorException {
        try {
            return validate(in, CryptixPGPUtils.toPublicKey(armouredKey));
        } catch (Exception e) {
            throw new SignatureValidatorException("while verifying signature", e);
        }
    }

    public boolean validate(InputStream in, PublicKey key) throws SignatureValidatorException {
        try {
            Message msg = CryptixPGPUtils.getMessage(in);
            if (!(msg instanceof SignedMessage))
                throw new SignatureValidatorException("message is not signed");
            return ((SignedMessage) msg).verify(key);
        } catch (Exception e) {
            throw new SignatureValidatorException("while verifying signature", e);
        }
    }
}
