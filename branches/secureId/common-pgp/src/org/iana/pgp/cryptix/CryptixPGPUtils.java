package org.iana.pgp.cryptix;

import cryptix.message.*;
import cryptix.pki.KeyBundle;
import cryptix.pki.KeyBundleException;
import cryptix.openpgp.PGPArmouredMessage;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.util.Collection;

/**
 * @author Jakub Laszkiewicz
 */
public class CryptixPGPUtils {
    static {
        java.security.Security.addProvider(new cryptix.jce.provider.CryptixCrypto() );
        java.security.Security.addProvider(new cryptix.openpgp.provider.CryptixOpenPGP() );
    }

    private static String removeTrailingWhitespaces(String line) {
        if (line == null) return null;
        while (line.length() > 0 && (line.charAt(line.length() - 1) == ' ' || (line.charAt(line.length() - 1) == '\t'))) {
            line = line.substring(0, line.length() - 1);
        }
        return line;
    }

    public static Message getMessage(InputStream in) throws NoSuchAlgorithmException, IOException, MessageException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer buf = new StringBuffer();
        String line = removeTrailingWhitespaces(reader.readLine());
        if (line != null) {
            buf.append(line);
            while ((line = removeTrailingWhitespaces(reader.readLine())) != null) {
                buf.append("\015\012");
                buf.append(line);
            }
        }
        ByteArrayInputStream bin = new ByteArrayInputStream(buf.toString().getBytes());
        MessageFactory mf = MessageFactory.getInstance("OpenPGP");
        Collection msgs = mf.generateMessages(bin);
        return (Message) msgs.iterator().next();
    }

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

    public static Message signMessage(Message msg, String privateKey, String passphrase) throws NoSuchAlgorithmException, MessageException, IOException, KeyBundleException, UnrecoverableKeyException {
        InputStream in = new ByteArrayInputStream(privateKey.getBytes());
        KeyBundle bundle;
        try {
            MessageFactory mf = MessageFactory.getInstance("OpenPGP");
            Collection msgs = mf.generateMessages(in);
            KeyBundleMessage kbm = (KeyBundleMessage) msgs.iterator().next();
            bundle = kbm.getKeyBundle();
        } finally {
            in.close();
        }
        SignedMessageBuilder smb = SignedMessageBuilder.getInstance("OpenPGP");
        smb.init(msg);
        smb.addSigner(bundle, passphrase.toCharArray());
        return smb.build();
    }

    public static String armourMessage(Message msg) throws MessageException, UnsupportedEncodingException {
        Message signed = new PGPArmouredMessage(msg);
        return new String(signed.getEncoded(), "US-ASCII");
    }
}
