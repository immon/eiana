package org.iana.rzm.facade.auth;

import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.criteria.Equal;
import org.iana.pgp.SignatureValidator;
import org.iana.pgp.SignatureValidatorException;
import org.iana.pgp.cryptix.CryptixSignatureValidator;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;


/**
 * @author Jakub Laszkiewicz
 */
public class PgpMailAuthenticator implements AuthenticationService {
    private UserManager manager;

    private Config config;

    public PgpMailAuthenticator(UserManager manager, ParameterManager parameterManager) {
        CheckTool.checkNull(manager, "user manager");
        CheckTool.checkNull(parameterManager, "parameter manager");
        this.manager = manager;
        config = new OwnedConfig(parameterManager);
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(data, "authentication data");

        PgpMailAuth pgpMailData = (PgpMailAuth) data;
        try {
            AuthenticatedUser authenticatedUser = findSpecialUser(pgpMailData);
            if (authenticatedUser != null) return authenticatedUser;

            List<RZMUser> users = manager.find(new Equal("email", pgpMailData.getEmail()));

            for (RZMUser user : users) {
                if (validateSignature(pgpMailData.getMessage(), user.getPublicKey()))
                    return new AuthenticatedUser(user.getObjId(), user.getLoginName(), user.isAdmin());
            }

            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} has not been found.", data.getUserName()));

        } catch (ConfigException e) {
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} authentication failed.", data.getUserName()),
                    e);
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

    private AuthenticatedUser findSpecialUser(PgpMailAuth pgpMailData) throws ConfigException, SignatureValidatorException, UnsupportedEncodingException {
        String email = config.getParameter(USDOC_EMAIL);
        if (email != null && email.equals(pgpMailData.getUserName())) {
            String publicKey = config.getParameter(USDOC_PUBLIC_KEY);
            if (validateSignature(pgpMailData.getMessage(), publicKey))
                return new USDoCAuthenticatedUser();
        }

        email = config.getParameter(VERISIGN_EMAIL);
        if (email != null && email.equals(pgpMailData.getUserName())) {
            String publicKey = config.getParameter(VERISIGN_PUBLIC_KEY);
            if (validateSignature(pgpMailData.getMessage(), publicKey))
                return new VerisignAuthenticatedUser();
        }

        return null;
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
