package org.iana.rzm.facade.auth;

import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.common.validators.CheckTool;

import java.text.MessageFormat;

/**
 * @author Jakub Laszkiewicz
 */
public class MailAuthenticator implements AuthenticationService {

    private UserManager manager;

    public MailAuthenticator(UserManager manager) {
        CheckTool.checkNull(manager, "user manager");
        this.manager = manager;
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(data, "authentication data");

        MailAuth mailData = (MailAuth) data;
        RZMUser user = mailData.getDomainName() == null ? manager.findUserByEmail(mailData.getUserName()) : manager.findUserByEmailAndRole(mailData.getUserName(), mailData.getDomainName());
        if (user == null) {
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} has not been found.", data.getUserName()));
        }
        return new AuthenticatedUser(user.getObjId(), user.getLoginName(), user.isAdmin());
    }

    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(token, "authentication token");
        CheckTool.checkNull(data, "authentication data");
        throw new AuthenticationRequiredException(Authentication.SECURID);
    }
}
