package org.iana.pgp;

import cryptix.message.Message;
import cryptix.message.SignedMessage;
import cryptix.message.LiteralMessage;
import org.iana.pgp.cryptix.CryptixPGPUtils;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;

/**
 * @author Jakub Laszkiewicz
 */
public class PGPUtils {
    public static PublicKey toPublicKey(String armouredKey) throws PGPUtilsException {
        try {
            return CryptixPGPUtils.toPublicKey(armouredKey);
        } catch (Exception e) {
            throw new PGPUtilsException("while converting to public key", e);
        }
    }

    public static String getSignedMessageContent(String content) throws PGPUtilsException {
        try {
            Message msg = CryptixPGPUtils.getMessage(new ByteArrayInputStream(content.getBytes("US-ASCII")));
            if (!(msg instanceof SignedMessage))
                throw new SignatureValidatorException("message is not signed");
            Message cont = ((SignedMessage) msg).getContents();
            if (!(cont instanceof LiteralMessage))
                throw new SignatureValidatorException("unexpected message content type");
            return new String(((LiteralMessage) cont).getBinaryData(), "US-ASCII");
        } catch (Exception e) {
            throw new PGPUtilsException("while getting signed message content", e);
        }
    }
}
