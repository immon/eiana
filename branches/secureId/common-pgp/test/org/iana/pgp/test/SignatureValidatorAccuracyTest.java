package org.iana.pgp.test;

import org.iana.pgp.SignatureValidator;
import org.iana.pgp.SignatureValidatorException;
import org.iana.pgp.PGPUtils;
import org.iana.pgp.PGPUtilsException;
import org.iana.pgp.cryptix.CryptixSignatureValidator;
import org.testng.annotations.Test;

import java.io.*;

/**
 * @author Jakub Laszkiewicz
 */
@Test
public class SignatureValidatorAccuracyTest {
    private static final String MESSAGE_FILE_NAME = "test-message.txt.asc";
    private static final String MESSAGE_CONTENT = "test message";
    private static final String MESSAGE_BROKEN_CONTENT_FILE_NAME = "test-message-broken-content.txt.asc";
    private static final String MESSAGE_ANOTHER_KEY_FILE_NAME = "test-message-another-key.txt.asc";
    private static final String KEY_FILE_NAME = "tester.pgp.asc";

    public void testValidateSuccessful() throws SignatureValidatorException, IOException {
        InputStream in = getClass().getResourceAsStream(MESSAGE_FILE_NAME);
        assert in != null;
        try {
            String arm = SignatureValidatorTestUtil.loadFromFile(KEY_FILE_NAME);
            SignatureValidator validator = new CryptixSignatureValidator();
            assert validator.validate(in, arm);
        } finally {
            in.close();
        }
    }

    public void testGetContent() throws SignatureValidatorException, IOException, PGPUtilsException {
        String content = PGPUtils.getSignedMessageContent(SignatureValidatorTestUtil.loadFromFile(MESSAGE_FILE_NAME));
        assert MESSAGE_CONTENT.equals(content);
    }

    public void testValidateUnsuccessfulWrongContent() throws SignatureValidatorException, IOException {
        InputStream in = getClass().getResourceAsStream(MESSAGE_BROKEN_CONTENT_FILE_NAME);
        assert in != null;
        try {
            String arm = SignatureValidatorTestUtil.loadFromFile(KEY_FILE_NAME);
            SignatureValidator validator = new CryptixSignatureValidator();
            assert !validator.validate(in, arm);
        } finally {
            in.close();
        }
    }

    public void testValidateUnsuccessfulAnotherKey() throws SignatureValidatorException, IOException {
        InputStream in = getClass().getResourceAsStream(MESSAGE_ANOTHER_KEY_FILE_NAME);
        assert in != null;
        try {
            String arm = SignatureValidatorTestUtil.loadFromFile(KEY_FILE_NAME);
            SignatureValidator validator = new CryptixSignatureValidator();
            assert !validator.validate(in, arm);
        } finally {
            in.close();
        }
    }
}
