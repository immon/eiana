package org.iana.pgp.cryptix;

import cryptix.message.Message;
import cryptix.message.MessageException;
import cryptix.message.MessageFactory;
import cryptix.message.SignedMessage;
import org.iana.pgp.SignatureValidator;
import org.iana.pgp.SignatureValidatorException;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Collection;

/**
 * @author Jakub Laszkiewicz
 */
public class CryptixSignatureValidator implements SignatureValidator {
    static {
        java.security.Security.addProvider(new cryptix.jce.provider.CryptixCrypto());
        java.security.Security.addProvider(new cryptix.openpgp.provider.CryptixOpenPGP());
    }


    private String removeTrailingWhitespaces(String line) {
        if (line == null) return null;
        while (line.length() > 0 && (line.charAt(line.length() - 1) == ' ' || (line.charAt(line.length() - 1) == '\t'))) {
            line = line.substring(0, line.length() - 1);
        }
        return line;
    }

    private Message getMessage(InputStream in) throws NoSuchAlgorithmException, IOException, MessageException {
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

    public boolean validate(InputStream in, String armouredKey) throws SignatureValidatorException {
        try {
            return validate(in, CryptixPGPUtils.toPublicKey(armouredKey));
        } catch (Exception e) {
            throw new SignatureValidatorException("while verifying signature", e);
        }
    }

    public boolean validate(InputStream in, PublicKey key) throws SignatureValidatorException {
        try {
            Message msg = getMessage(in);
            if (!(msg instanceof SignedMessage))
                throw new SignatureValidatorException("message is not signed");
            return ((SignedMessage) msg).verify(key);
        } catch (Exception e) {
            throw new SignatureValidatorException("while verifying signature", e);
        }
    }
}
