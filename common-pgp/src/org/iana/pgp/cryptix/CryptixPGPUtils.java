package org.iana.pgp.cryptix;

import cryptix.message.KeyBundleMessage;
import cryptix.message.MessageException;
import cryptix.message.MessageFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Collection;

/**
 * @author Jakub Laszkiewicz
 */
public class CryptixPGPUtils {
    public static PublicKey toPublicKey(String armouredKey) throws IOException, NoSuchAlgorithmException, MessageException {
        InputStream in = new ByteArrayInputStream(armouredKey.getBytes());
        try {
            MessageFactory mf = MessageFactory.getInstance("OpenPGP");
            Collection msgs = mf.generateMessages(in);
            KeyBundleMessage kbm = (KeyBundleMessage) msgs.iterator().next();
            return (PublicKey) kbm.getKeyBundle().getPublicKeys().next();
        } finally {
            in.close();
        }
    }
}
