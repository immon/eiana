package org.iana.pgp.test;

import org.iana.pgp.PGPUtils;
import org.iana.pgp.PGPUtilsException;
import org.iana.pgp.SignatureValidator;
import org.iana.pgp.SignatureValidatorException;
import org.iana.pgp.cryptix.CryptixSignatureValidator;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 * @author Jakub Laszkiewicz
 */
@Test
public class PGPUtilsTest {

    private static final String TEST_CONTENT =
            "This is a text message \n" +
                    "This is another line\n";
    private static final String KEY_FILE_NAME = "tester.secret.pgp.asc";
    private static final String KEY_PASSPHRASE = "tester";

    public void signMessageTest() throws IOException, PGPUtilsException, SignatureValidatorException {
        String key = SignatureValidatorTestUtil.loadFromFile(KEY_FILE_NAME);
        String signed = PGPUtils.signMessage(TEST_CONTENT, key, KEY_PASSPHRASE);
        SignatureValidator validator = new CryptixSignatureValidator();
        assert validator.validate(new ByteArrayInputStream(signed.getBytes("US-ASCII")), key);
    }
}
