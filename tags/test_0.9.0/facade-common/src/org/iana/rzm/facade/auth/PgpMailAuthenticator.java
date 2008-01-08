package org.iana.rzm.facade.auth;

import org.iana.criteria.Equal;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.pgp.SignatureValidator;
import org.iana.pgp.SignatureValidatorException;
import org.iana.pgp.cryptix.CryptixSignatureValidator;

import java.text.MessageFormat;
import java.util.List;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Jakub Laszkiewicz
 */
public class PgpMailAuthenticator implements AuthenticationService {
    private UserManager manager;

    public PgpMailAuthenticator(UserManager manager) {
        CheckTool.checkNull(manager, "user manager");
        this.manager = manager;
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(data, "authentication data");

        PgpMailAuth pgpMailData = (PgpMailAuth) data;
        List<RZMUser> users = manager.find(new Equal("email", pgpMailData.getEmail()));
        try {
            for (RZMUser user : users) {
                if (validateSignature(pgpMailData.getMessage(), user.getPublicKey()))
                    return new AuthenticatedUser(user.getObjId(), user.getLoginName(), user.isAdmin());
            }
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} has not been found.", data.getUserName()));
        } catch (SignatureValidatorException e) {
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} authentication failed.", data.getUserName()),
                    e);
        } catch (UnsupportedEncodingException e) {
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} authentication failed.", data.getUserName()),
                    e);
        }
    }

    private boolean validateSignature(String message, String publicKey) throws SignatureValidatorException, UnsupportedEncodingException {
        if (message == null || publicKey == null) return false;
        SignatureValidator validator = new CryptixSignatureValidator();
        InputStream in = new ByteArrayInputStream(message.getBytes("US-ASCII"));
        return validator.validate(in, publicKey);
    }

    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(token, "authentication token");
        CheckTool.checkNull(data, "authentication data");
        throw new AuthenticationRequiredException(Authentication.SECURID);
    }
}
