package org.iana.pgp.test;

import org.iana.pgp.SignatureValidatorException;
import org.iana.pgp.SignatureValidator;
import org.iana.pgp.cryptix.CryptixSignatureValidator;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jakub Laszkiewicz
 */
@Test
public class SignatureValidatorFailureTest {
    private static final String MESSAGE_UNSIGNED_FILE_NAME = "test-message-unsigned.txt.asc";
    private static final String MESSAGE_BROKEN_SIGNATURE_FILE_NAME = "test-message-broken-signature.txt.asc";
    private static final String KEY_FILE_NAME = "tester.pgp.asc";

    @Test (expectedExceptions = {SignatureValidatorException.class})
    public void testValidateUnsigned() throws SignatureValidatorException, IOException {
        InputStream in = getClass().getResourceAsStream(MESSAGE_UNSIGNED_FILE_NAME);
        assert in != null;
        try {
            String arm = SignatureValidatorTestUtil.loadFromFile(KEY_FILE_NAME);
            SignatureValidator validator = new CryptixSignatureValidator();
            assert validator.validate(in, arm);
        } finally {
            in.close();
        }
    }

    @Test (expectedExceptions = {SignatureValidatorException.class})
    public void testValidateBrokenSignature() throws SignatureValidatorException, IOException {
        InputStream in = getClass().getResourceAsStream(MESSAGE_BROKEN_SIGNATURE_FILE_NAME);
        assert in != null;
        try {
            String arm = SignatureValidatorTestUtil.loadFromFile(KEY_FILE_NAME);
            SignatureValidator validator = new CryptixSignatureValidator();
            assert validator.validate(in, arm);
        } finally {
            in.close();
        }
    }
}
