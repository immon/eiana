package org.iana.rzm.web.services;

import org.apache.hivemind.lib.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.model.*;

public class RzmAuthenticationServiceImpl implements RzmAuthenticationService{

    private AuthenticationService authenticationService;
    private PasswordChangeService changePasswordService;

    public WebUser login(String userName, String password) throws AuthenticationFailedException, AuthenticationRequiredException {
        AuthenticatedUser user = authenticationService.authenticate(new PasswordAuth(userName, password));
        return new WebUser(user);
    }

    public WebUser secureId(String userName, String code) throws AuthenticationFailedException, AuthenticationRequiredException {
        AuthenticatedUser user = authenticationService.authenticate(new SecurIDAuth(userName, code));
        return new WebUser(user);
    }

    public void newPassword(String newPassword, String userName, String token, String newPasswordConfirmed) throws PasswordChangeException {
        changePasswordService.finishPasswordChange(userName,token,newPassword,newPasswordConfirmed);
    }

    public void resetPassword(String userName, String url, String token)throws PasswordChangeException {
        try {
            changePasswordService.initPasswordChange(userName, url, token);
        } catch (InfrastructureException e) {
            throw new RzmApplicationException(e);
        }
    }

    public void setBeanFactoryHolder(SpringBeanFactoryHolder beanFactoryHolder) {
        authenticationService = (AuthenticationService) beanFactoryHolder.getBeanFactory().getBean("authenticationServiceBean");
        changePasswordService = (PasswordChangeService)beanFactoryHolder.getBeanFactory().getBean("passwordChangeService");
    }

}
