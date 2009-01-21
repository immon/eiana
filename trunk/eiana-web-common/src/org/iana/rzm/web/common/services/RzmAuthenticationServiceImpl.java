package org.iana.rzm.web.common.services;

import org.apache.hivemind.lib.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.secureid.*;

public class RzmAuthenticationServiceImpl implements RzmAuthenticationService {

    private AuthenticationService authenticationService;
    private PasswordChangeService changePasswordService;
    private SecurIDService securIDService;


    public WebUser login(String userName, String password)
        throws AuthenticationFailedException, AuthenticationRequiredException {
        AuthenticatedUser user = authenticationService.authenticate(new PasswordAuth(userName, password));
        return new WebUser(user);
    }

    public WebUser secureId(AuthenticationToken authenticationToken, String userName, String code) throws AuthenticationFailedException, AuthenticationRequiredException {
        AuthenticatedUser user = authenticationService.authenticate(authenticationToken,new SecurIDAuth(userName, code));
        return new WebUser(user);
    }

    public void newPin(String sessionId, String pin) throws SecurIDException {
        securIDService.setPin(sessionId, pin);
    }

    public WebUser nextCode(AuthenticationToken authenticationToken, String sessionId, String code)
        throws SecurIDException, AuthenticationRequiredException {
        AuthenticatedUser user = securIDService.authenticateWithNextCode(authenticationToken,sessionId, code);
        return new WebUser(user);
    }

    public RSAPinData getPinInfo(String sessionId) throws SecurIDException {
        return securIDService.getPinInfo(sessionId);
    }

    public void newPassword(String newPassword, String userName, String token, String newPasswordConfirmed)
        throws PasswordChangeException {
        try {
            changePasswordService.finishPasswordChange(userName, token, newPassword, newPasswordConfirmed);
        } catch (InfrastructureException e) {
            throw new RzmApplicationException(e);
        }
    }

    public void resetPassword(String userName, String url, String token) throws PasswordChangeException {
        try {
            changePasswordService.initPasswordChange(userName, url, token);
        } catch (InfrastructureException e) {
            throw new RzmApplicationException(e);
        }
    }

    public String recoverUser(String email, String password)
        throws NonUniqueDataToRecoverUserException, NoObjectFoundException {

        try {
            UserVO vo = changePasswordService.recoverUser(email, password);
            return vo.getUserName();
        } catch (InfrastructureException e) {
            throw new RzmApplicationException(e);
        }
    }

    public void setBeanFactoryHolder(SpringBeanFactoryHolder beanFactoryHolder) {
        authenticationService =
            (AuthenticationService) beanFactoryHolder.getBeanFactory().getBean("remoteAuthenticationServiceBean");
        changePasswordService =
            (PasswordChangeService) beanFactoryHolder.getBeanFactory().getBean("remotePasswordChangeService");
        securIDService = (SecurIDService) beanFactoryHolder.getBeanFactory().getBean("remoteSecureIdServiceBean");
    }

}
