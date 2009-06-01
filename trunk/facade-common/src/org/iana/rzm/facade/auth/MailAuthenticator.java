package org.iana.rzm.facade.auth;

import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;

import java.text.MessageFormat;

/**
 * @author Jakub Laszkiewicz
 */
public class MailAuthenticator implements AuthenticationService {

    private UserManager manager;
    private Config config;

    public MailAuthenticator(UserManager manager, ParameterManager parameterManager) {
        CheckTool.checkNull(manager, "user manager");
        CheckTool.checkNull(parameterManager, "parameter manager");
        this.manager = manager;
        this.config = new OwnedConfig(parameterManager);
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(data, "authentication data");

        MailAuth mailData = (MailAuth) data;

        try {
            AuthenticatedUser authenticatedUser = findSpecialUser(mailData);
            if (authenticatedUser != null)
                return authenticatedUser;

        } catch (ConfigException e) {
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} authentication failed.", data.getUserName()),
                    e);
        }

        RZMUser user = mailData.getDomainName() == null ? manager.findUserByEmail(mailData.getUserName()) : manager.findUserByEmailAndRole(mailData.getUserName(), mailData.getDomainName());
        if (user == null) {
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} has not been found.", data.getUserName()));
        }
        return new AuthenticatedUser(user.getObjId(), user.getLoginName(), user.isAdmin(), user.isRoot());
    }

    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(token, "authentication token");
        CheckTool.checkNull(data, "authentication data");
        throw new AuthenticationRequiredException(Authentication.SECURID);
    }
    
    private AuthenticatedUser findSpecialUser(MailAuth mailData) throws ConfigException
        {
        String email = config.getParameter(USDOC_EMAIL);
        if (email != null && email.equals(mailData.getUserName())) {
            return new USDoCAuthenticatedUser();
        }

        email = config.getParameter(VERISIGN_EMAIL);
        if (email != null && email.equals(mailData.getUserName())) {
            return new VerisignAuthenticatedUser();
        }

        return null;
    }

}
